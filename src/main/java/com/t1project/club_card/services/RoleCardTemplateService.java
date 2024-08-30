package com.t1project.club_card.services;

import com.t1project.club_card.models.RoleCardTemplate;
import com.t1project.club_card.repositories.RoleCardTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleCardTemplateService {

    @Autowired
    private RoleCardTemplateRepository roleCardTemplateRepository;

    public Mono<Set<Integer>> addTemplate(int number) {
        return roleCardTemplateRepository.findByRole("ROLE_SUPERUSER")
                .flatMap(roleCardTemplate -> {
                    Set<Integer> templates = roleCardTemplate.getTemplates();
                    templates.add(number);
                    roleCardTemplate.setTemplates(templates);
                    return roleCardTemplateRepository.save(roleCardTemplate).map(RoleCardTemplate::getTemplates);
                });
    }
}
