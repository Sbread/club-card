package com.t1project.club_card.repositories;

import com.t1project.club_card.models.TemplatePrivilege;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TemplatePrivilegesRepository extends ReactiveCrudRepository<TemplatePrivilege, Integer> {
    Mono<TemplatePrivilege> findByTemplate(String template);
}
