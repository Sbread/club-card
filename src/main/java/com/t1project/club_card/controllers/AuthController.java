package com.t1project.club_card.controllers;

import com.t1project.club_card.dto.*;
import com.t1project.club_card.exceptions.EmailExistedException;
import com.t1project.club_card.exceptions.InvalidFieldException;
import com.t1project.club_card.exceptions.RefreshTokenExpiredException;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.services.BlacklistTokenService;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.security.CustomReactiveAuthenticationManager;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.services.RefreshTokenService;
import com.t1project.club_card.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin
@Slf4j
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
    public Mono<ResponseEntity<JwtResponseDTO>> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO,
                                                                        ServerWebExchange exchange) {
        System.out.println(authRequestDTO.getEmail());
        Authentication authenticationToken
                = new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword());
        return customReactiveAuthenticationManager.authenticate(authenticationToken)
                .flatMap(authentication ->
                        Mono.zip(jwtService.GenerateToken(authRequestDTO.getEmail()),
                                        refreshTokenService.createRefreshToken(authRequestDTO.getEmail()))
                                .flatMap(tuple -> {
                                    JwtResponseDTO dto = Utils.mapToJwtResponse(tuple.getT1());
                                    ResponseCookie refreshCookie
                                            = refreshTokenService.makeCookie("refreshToken", tuple.getT2().getToken());
                                    exchange.getResponse().addCookie(refreshCookie);
                                    return Mono.just(ResponseEntity.ok().body(dto));
                                })
                )
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid user credentials")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Authentication failed", e)));
    }

    @PostMapping("/refreshToken")
    public Mono<ResponseEntity<JwtResponseDTO>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            ServerWebExchange exchange) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .flatMap(refreshTokenO -> clubMemberRepository.findById(refreshTokenO.getClubMemberId())
                        .flatMap(clubMember -> jwtService.GenerateToken(clubMember.getEmail())
                                .flatMap(accessToken -> refreshTokenService.updateToken(refreshTokenO)
                                        .flatMap(updatedToken -> {
                                            ResponseCookie refreshTokenCookie
                                                    = refreshTokenService.makeCookie("refreshToken", updatedToken);
                                            JwtResponseDTO jwtResponse = Utils.mapToJwtResponse(accessToken);
                                            exchange.getResponse().addCookie(refreshTokenCookie);
                                            return Mono.just(ResponseEntity.ok().body(jwtResponse));
                                        }))))
                .switchIfEmpty(Mono.error(new RefreshTokenExpiredException("Refresh token expired")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Smth went wrong")));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<ResponseClubMemberDTO>> registerClubMember(@RequestBody RegisterRequestDTO clubMember) {
        if (!Utils.validateEmail(clubMember.getEmail())) {
            throw new InvalidFieldException("Invalid email");
        }
        if (!Utils.validatePhone(clubMember.getPhone())) {
            throw new InvalidFieldException("Invalid phone");
        }
        return clubMemberService.registerClubMember(clubMember)
                .map(Utils::mapToResponseDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .onErrorResume(e -> Mono.error(new EmailExistedException("email already exist")));
    }

    @GetMapping("/logout")
    public Mono<ResponseEntity<LogoutDTO>> logout(@RequestHeader("Authorization") String authHeader,
                                               @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String token = Utils.extractBearerToken(authHeader);
        return refreshTokenService.deleteByToken(refreshToken).then(blacklistTokenService.saveToken(token)
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body(LogoutDTO.builder().status("successful").build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(LogoutDTO.builder().status("deny").build()))));
    }
}
