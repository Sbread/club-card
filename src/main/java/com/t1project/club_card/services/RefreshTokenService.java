package com.t1project.club_card.services;

import com.t1project.club_card.exceptions.RefreshTokenExpiredException;
import com.t1project.club_card.models.RefreshToken;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final int MILLIS_LIVING_TIME = 900_000;


    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    ClubMemberRepository clubMemberRepository;

    private RefreshToken buildRefreshToken(int id) {
        return RefreshToken.builder()
                .clubMemberId(id)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(MILLIS_LIVING_TIME))
                .build();
    }

    private RefreshToken updToken(RefreshToken refreshToken) {
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(MILLIS_LIVING_TIME));
        return refreshToken;
    }

    public Mono<String> updateToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(updToken(refreshToken))
                .map(RefreshToken::getToken);
    }

    public ResponseCookie makeCookie(String name, String updatedToken) {
        return ResponseCookie.from(name, updatedToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
    }

    public Mono<RefreshToken> createRefreshToken(String username) {
        return clubMemberRepository.findByEmail(username).flatMap(
                clubMember -> {
                    RefreshToken refreshToken = buildRefreshToken(clubMember.getId());
                    return refreshTokenRepository.save(refreshToken);
                }
        );
    }

    public Mono<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Mono<Void> deleteByToken(String token) {
        return refreshTokenRepository.deleteByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token is expired. Please make a new login..!");
        }
        return token;
    }
}
