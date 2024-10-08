package com.t1project.club_card.services;

import com.t1project.club_card.models.TemplatePrivilege;
import com.t1project.club_card.repositories.TemplatePrivilegesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class TemplatePrivilegeService {

    @Autowired
    private TemplatePrivilegesRepository templatePrivilegesRepository;

    @Transactional(readOnly = true)
    public Mono<TemplatePrivilege> findByTemplate(String template) {
        return templatePrivilegesRepository.findByTemplate(template);
    }

    @Transactional
    public Mono<TemplatePrivilege> save(TemplatePrivilege templatePrivilege) {
        return templatePrivilegesRepository.save(templatePrivilege);
    }

    @Transactional(readOnly = true)
    public Flux<TemplatePrivilege> findAll() {
        return templatePrivilegesRepository.findAll();
    }

    public Mono<Map<String, Set<String>>> createTemplatePrivilegeMap() {
        return findAllTemplates()
                .flatMap(template ->
                        findByTemplate(template)
                                .map(templatePrivilege -> Map.entry(template, templatePrivilege.getPrivileges()))
                )
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    @Transactional(readOnly = true)
    public Flux<String> findAllTemplates() {
        return templatePrivilegesRepository.findAll().map(TemplatePrivilege::getTemplate);
    }

    public Mono<Map<String, Set<String>>> invertTemplatePrivilegeMap() {
        return createTemplatePrivilegeMap()
                .flatMapMany(templatePrivilegeMap -> Flux.fromIterable(templatePrivilegeMap.entrySet()))
                .flatMap(entry -> {
                    String template = entry.getKey();
                    Set<String> privileges = entry.getValue();
                    return Flux.fromIterable(privileges)
                            .map(privilege -> Map.entry(privilege, template));
                })
                .collectMultimap(Map.Entry::getKey, Map.Entry::getValue)
                .map(multimap -> {
                    Map<String, Set<String>> invertedMap = new HashMap<>();
                    multimap.forEach((key, valueList) -> invertedMap.put(key, new HashSet<>(valueList)));
                    return invertedMap;
                });
    }

    public Mono<Set<String>> getTemplatesByPrivilege(String privilege) {
        return invertTemplatePrivilegeMap()
                .flatMap(invertedMap -> {
                    Set<String> templates = invertedMap.get(privilege);
                    return Mono.just(Objects.requireNonNullElse(templates, Collections.emptySet()));
                });
    }
}
