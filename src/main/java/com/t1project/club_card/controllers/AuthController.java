package com.t1project.club_card.controllers;

import com.t1project.club_card.dto.AuthRequestDTO;
import com.t1project.club_card.dto.JwtResponseDTO;
import com.t1project.club_card.dto.RefreshTokenRequestDTO;
import com.t1project.club_card.dto.RegisterRequestDTO;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.security.CustomReactiveAuthenticationManager;
import com.t1project.club_card.services.JWTService;
import com.t1project.club_card.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Controller
public class AuthController {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ClubMemberService clubMemberService;

    @Autowired
    private CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public Mono<JwtResponseDTO> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword());
        return customReactiveAuthenticationManager.authenticate(authenticationToken)
                .flatMap(authentication -> refreshTokenService.createRefreshToken(authRequestDTO.getUsername())
                        .map(refreshToken -> JwtResponseDTO.builder()
                                .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                                .token(refreshToken.getToken())
                                .build()))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid user credentials")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Authentication failed", e)));
    }

    @PostMapping("/refreshToken")
    public Mono<JwtResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .flatMap(refreshToken -> clubMemberRepository.findById(refreshToken.getClubMemberId()))
                .map(clubMember -> jwtService.GenerateToken(clubMember.getUsername()))
                .map(accessToken -> JwtResponseDTO.builder()
                        .accessToken(accessToken)
                        .token(refreshTokenRequestDTO.getToken())
                        .build())
                .switchIfEmpty(Mono.error(new RuntimeException("Cannot find Refresh Token in DB")));
    }


    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerClubMember(@RequestBody RegisterRequestDTO clubMember) {
        return clubMemberService.registerClubMember(clubMember)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body("Registered successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Registration failed\n" + e.getMessage())));
    }
}
