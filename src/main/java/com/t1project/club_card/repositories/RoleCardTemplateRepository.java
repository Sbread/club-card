package com.t1project.club_card.repositories;

import com.t1project.club_card.models.RoleCardTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleCardTemplateRepository extends ReactiveCrudRepository<RoleCardTemplate, Integer> {
    Mono<RoleCardTemplate> findByRole(String role);
}
