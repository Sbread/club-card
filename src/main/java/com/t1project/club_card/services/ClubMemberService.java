package com.t1project.club_card.services;

import com.t1project.club_card.dto.RegisterRequestDTO;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.repositories.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClubMemberService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClubMember getCurrentClubMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof ClubMember) {
                return (ClubMember) principal;
            }
        }
        return null;
    }

    public Mono<ClubMember> registerClubMember(RegisterRequestDTO registerRequestDTO) {
        final Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        final Set<String> privileges = new HashSet<>();
        privileges.add("STANDARD");
        final ClubMember clubMember = ClubMember.builder()
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .firstName("")
                .lastName("")
                .email(registerRequestDTO.getEmail())
                .phoneNumber("")
                .roles(roles)
                .privilege(privileges).build();
        return clubMemberRepository.save(clubMember);
    }
}
