package com.t1project.club_card.services;

import com.t1project.club_card.models.BlacklistToken;
import com.t1project.club_card.repositories.BlacklistTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BlacklistTokenService {

    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository;

    public Mono<BlacklistToken> saveToken(String token) {
        return blacklistTokenRepository.save(BlacklistToken.builder().token(token).build());
    }

    public Mono<Boolean> existsByToken(String token) {
        return blacklistTokenRepository.existsByToken(token);
    }
}
