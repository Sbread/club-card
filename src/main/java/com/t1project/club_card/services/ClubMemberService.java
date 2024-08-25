package com.t1project.club_card.services;

import com.t1project.club_card.utils.Utils;
import com.t1project.club_card.dto.RegisterRequestDTO;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.repositories.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClubMemberService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;


    public Mono<ClubMember> registerClubMember(RegisterRequestDTO registerRequestDTO) {
        final Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        final Set<String> privileges = new HashSet<>();
        privileges.add("STANDARD");
        final ClubMember clubMember = ClubMember.builder()
                .username(registerRequestDTO.getUsername())
                .password(Utils.bCryptPasswordEncoder.encode(registerRequestDTO.getPassword()))
                .firstName(null)
                .lastName(null)
                .email(registerRequestDTO.getEmail())
                .phoneNumber(null)
                .roles(roles)
                .privilege(privileges).build();
        System.out.println(clubMember.toString());
        return clubMemberRepository.save(clubMember);
    }

    public Mono<ClubMember> changeEmail(String username, String newEmail) {
        return clubMemberRepository.findMemberByUsername(username)
                .flatMap(clubMember -> {
                    clubMember.setEmail(newEmail);
                    return clubMemberRepository.save(clubMember);
                });
    }

    public Mono<ClubMember> changePhoneNumber(String username, String newPhoneNumber) {
        return clubMemberRepository.findMemberByUsername(username)
                .flatMap(clubMember -> {
                    clubMember.setPhoneNumber(newPhoneNumber);
                    return clubMemberRepository.save(clubMember);
                });
    }

    public Mono<ClubMember> changePassword(String username, String newPassword) {
        return clubMemberRepository.findMemberByUsername(username)
                .flatMap(clubMember -> {
                    clubMember.setPassword(Utils.bCryptPasswordEncoder.encode(newPassword));
                    return clubMemberRepository.save(clubMember);
                });
    }
}
