package com.t1project.club_card.services;

import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.repositories.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClubAdminService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    public Flux<ClubMember> getAllClubMembers() {
        return clubMemberRepository.findAll();
    }

    public Mono<ClubMember> changeUsernameRole(String username, String newRole) {
        return clubMemberRepository.findMemberByUsername(username)
                .flatMap(clubMember -> {
                    Set<String> roles = clubMember.getRoles();
                    if (roles.contains("ROLE_SUPERUSER") || roles.contains("ROLE_ADMIN")) {
                        return Mono.error(new IllegalAccessException("Admin cannot change superusers and admins role"));
                    }
                    Set<String> newRoles = new HashSet<>();
                    newRoles.add(newRole);
                    clubMember.setRoles(newRoles);
                    return clubMemberRepository.save(clubMember);
                });
    }

    public Mono<ClubMember> changeUsernamePrivilege(String username, String newPrivilege) {
        return clubMemberRepository.findMemberByUsername(username)
                .flatMap(clubMember -> {
                    Set<String> roles = clubMember.getRoles();
                    if (roles.contains("ROLE_SUPERUSER") || roles.contains("ROLE_ADMIN")) {
                        return Mono.error(
                                new IllegalAccessException("Admin cannot change superusers and admins privilege"));
                    }
                    clubMember.setPrivilege(newPrivilege);
                    return clubMemberRepository.save(clubMember);
                });
    }

    public Mono<ClubMember> changeUsernameIsLocked(String username, boolean newIsLocked) {
        return clubMemberRepository.findMemberByUsername(username)
                .flatMap(clubMember -> {
                    Set<String> roles = clubMember.getRoles();
                    if (roles.contains("ROLE_SUPERUSER") || roles.contains("ROLE_ADMIN")) {
                        return Mono.error(
                                new IllegalAccessException("Admin cannot change superusers and admins locked"));
                    }
                    clubMember.setLocked(newIsLocked);
                    return clubMemberRepository.save(clubMember);
                });
    }
}
