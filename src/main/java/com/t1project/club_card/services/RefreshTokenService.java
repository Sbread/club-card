package com.t1project.club_card.services;

import com.t1project.club_card.exceptions.RefreshTokenExpiredException;
import com.t1project.club_card.models.RefreshToken;
import com.t1project.club_card.repositories.ClubMemberRepository;
import com.t1project.club_card.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Mono<RefreshToken> createRefreshToken(String username) {
        return clubMemberRepository.findByEmail(username).flatMap(
                clubMember -> {
                    RefreshToken refreshToken = RefreshToken.builder()
                            .clubMemberId(clubMember.getId())
                            .token(UUID.randomUUID().toString())
                            .expiryDate(Instant.now().plusMillis(MILLIS_LIVING_TIME))
                            .build();
                    return refreshTokenRepository.save(refreshToken);
                }
        );
    }

    public Mono<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Refresh token is expired. Please make a new login..!");
        }
        return token;
    }
}
