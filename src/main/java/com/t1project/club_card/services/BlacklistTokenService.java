package com.t1project.club_card.services;

import com.t1project.club_card.models.BlacklistToken;
import com.t1project.club_card.repositories.BlacklistTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class BlacklistTokenService {

    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository;

    @Transactional
    public Mono<BlacklistToken> saveToken(String token) {
        return blacklistTokenRepository.save(BlacklistToken.builder().token(token).build());
    }

    @Transactional(readOnly = true)
    public Mono<Boolean> existsByToken(String token) {
        return blacklistTokenRepository.existsByToken(token);
    }
}
