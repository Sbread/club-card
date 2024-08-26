package com.t1project.club_card.repositories;

import com.t1project.club_card.models.BlacklistToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BlacklistTokenRepository extends ReactiveCrudRepository<BlacklistToken, Integer> {
    Mono<Boolean> existsByToken(String token);
}
