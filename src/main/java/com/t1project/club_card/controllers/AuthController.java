package com.t1project.club_card.controllers;

import com.t1project.club_card.dto.*;
import com.t1project.club_card.exceptions.RefreshTokenExpiredException;
import com.t1project.club_card.models.RefreshToken;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.services.BlacklistTokenService;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.security.CustomReactiveAuthenticationManager;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.services.RefreshTokenService;
import com.t1project.club_card.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
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
    public Mono<JwtResponseDTO> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        System.out.println(authRequestDTO.getEmail());
        Authentication authenticationToken
                = new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword());
        System.out.println(authRequestDTO.getEmail());
        return customReactiveAuthenticationManager.authenticate(authenticationToken)
                .flatMap(authentication ->
                        Mono.zip(jwtService.GenerateToken(authRequestDTO.getEmail()),
                                        refreshTokenService.createRefreshToken(authRequestDTO.getEmail()))
                                .map(tuple -> {
                                    String accessToken = tuple.getT1();
                                    RefreshToken refreshToken = tuple.getT2();
                                    return JwtResponseDTO.builder()
                                            .accessToken(accessToken)
                                            .token(refreshToken.getToken())
                                            .build();
                                })
                )
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid user credentials")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Authentication failed", e)));
    }

    @PostMapping("/refreshToken")
    public Mono<JwtResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .flatMap(refreshToken -> clubMemberRepository.findById(refreshToken.getClubMemberId()))
                .flatMap(clubMember -> jwtService.GenerateToken(clubMember.getEmail()))
                .map(accessToken -> JwtResponseDTO.builder()
                        .accessToken(accessToken)
                        .token(refreshTokenRequestDTO.getToken())
                        .build())
                .switchIfEmpty(Mono.error(new RefreshTokenExpiredException("Refresh token expired")))
                .onErrorResume(e -> Mono.error(new RefreshTokenExpiredException("Refresh token expired")));
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
