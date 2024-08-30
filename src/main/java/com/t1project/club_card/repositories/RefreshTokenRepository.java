package com.t1project.club_card.repositories;

import com.t1project.club_card.models.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Integer> {
    Mono<RefreshToken> findByToken(String token);

    Mono<Void> deleteByToken(String token);
}
