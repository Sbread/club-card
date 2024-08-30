package com.t1project.club_card.services;

import com.t1project.club_card.models.RoleCardTemplate;
import com.t1project.club_card.repositories.RoleCardTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RoleCardTemplateService {

    @Autowired
    private RoleCardTemplateRepository roleCardTemplateRepository;

    public Mono<RoleCardTemplate> findByRole(String role) {
        return roleCardTemplateRepository.findByRole(role);
    }

    public Mono<RoleCardTemplate> save(RoleCardTemplate roleCardTemplate) {
        return roleCardTemplateRepository.save(roleCardTemplate);
    }
}
