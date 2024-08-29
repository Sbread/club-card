package com.t1project.club_card.controllers;

import com.t1project.club_card.dto.*;
import com.t1project.club_card.exceptions.RefreshTokenExpiredException;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.services.BlacklistTokenService;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.security.CustomReactiveAuthenticationManager;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.services.RefreshTokenService;
import com.t1project.club_card.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private BlacklistTokenService blacklistTokenService;

    @Autowired
    private CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public Mono<ServerResponse> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        System.out.println(authRequestDTO.getEmail());
        Authentication authenticationToken
                = new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword());
        System.out.println(authRequestDTO.getEmail());
        return customReactiveAuthenticationManager.authenticate(authenticationToken)
                .flatMap(authentication ->
                        Mono.zip(jwtService.GenerateToken(authRequestDTO.getEmail()),
                                        refreshTokenService.createRefreshToken(authRequestDTO.getEmail()))
                                .flatMap(tuple -> {
                                    JwtResponseDTO dto = Utils.mapToJwtResponse(tuple.getT1());
                                    ResponseCookie refreshCookie
                                            = makeCookie("refreshCookie", tuple.getT2().getToken());
                                    return ServerResponse.ok()
                                            .cookie(refreshCookie)
                                            .bodyValue(dto);
                                })
                )
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid user credentials")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Authentication failed", e)));
    }

    @PostMapping("/refreshToken")
    public Mono<ServerResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return Mono.error(new RuntimeException("Missing refreshToken"));
        }
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .flatMap(refreshTokenO -> clubMemberRepository.findById(refreshTokenO.getClubMemberId())
                        .flatMap(clubMember -> jwtService.GenerateToken(clubMember.getEmail())
                                .flatMap(accessToken -> refreshTokenService.updateToken(refreshTokenO)
                                        .flatMap(updatedToken -> {
                                            ResponseCookie refreshTokenCookie
                                                    = makeCookie("refreshToken", updatedToken);
                                            JwtResponseDTO jwtResponse = Utils.mapToJwtResponse(accessToken);
                                            return ServerResponse.ok()
                                                    .cookie(refreshTokenCookie)
                                                    .bodyValue(jwtResponse);
                                        }))))
                .switchIfEmpty(Mono.error(new RefreshTokenExpiredException("Refresh token expired")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Smth went wrong")));
    }

    private static ResponseCookie makeCookie(String name, String updatedToken) {
        return ResponseCookie.from(name, updatedToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
    }


    @PostMapping("/register")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> registerClubMember(@RequestBody RegisterRequestDTO clubMember) {
        return clubMemberService.registerClubMember(clubMember)
                .map(Utils::mapToResponseDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseClubMemberDTO.builder().build())));
    }

    @GetMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = Utils.extractBearerToken(authHeader);
        return blacklistTokenService.saveToken(token)
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body("Logout successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Logout failed\n" + e.getMessage())));
    }
}
