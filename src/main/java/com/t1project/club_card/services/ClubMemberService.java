package com.t1project.club_card.services;

import com.t1project.club_card.dto.ChangeAllUserFieldsDTO;
import com.t1project.club_card.utils.Utils;
import com.t1project.club_card.dto.RegisterRequestDTO;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.repositories.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClubMemberService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    public Mono<ClubMember> findById(Integer id) {
        return clubMemberRepository.findById(id);
    }

    public Mono<ClubMember> findByEmail(String email) {
        return clubMemberRepository.findByEmail(email);
    }

    public Flux<ClubMember> findAll() {
        return clubMemberRepository.findAll();
    }

    public Flux<ClubMember> findAllPaged(int page, int size) {
        return clubMemberRepository.findAllPaged(PageRequest.of(page, size));
    }

    public Mono<ClubMember> save(ClubMember clubMember) {
        return clubMemberRepository.save(clubMember);
    }

    public Mono<ClubMember> registerClubMember(RegisterRequestDTO registerRequestDTO) {
        final ClubMember clubMember = ClubMember.builder()
                .email(registerRequestDTO.getEmail())
                .password(Utils.bCryptPasswordEncoder.encode(registerRequestDTO.getPassword()))
                .firstName(registerRequestDTO.getFirstName())
                .lastName(registerRequestDTO.getLastName())
                .phoneNumber(registerRequestDTO.getPhone())
                .birthday(null)
                .privilege("STANDARD")
                .isLocked(false)
                .role("ROLE_USER")
                .build();
        System.out.println(clubMember.toString());
        return clubMemberRepository.save(clubMember);
    }

    public Mono<ClubMember> changeAllFields(Integer id, ChangeAllUserFieldsDTO fields) {
        return clubMemberRepository.findById(id)
                .flatMap(clubMember -> {
                    clubMember.setPassword(Utils.bCryptPasswordEncoder.encode(fields.getNewPassword()));
                    clubMember.setFirstName(fields.getNewFirstName());
                    clubMember.setLastName(fields.getNewLastName());
                    clubMember.setPhoneNumber(fields.getNewPhone());
                    clubMember.setBirthday(fields.getNewBirthday());
                    return clubMemberRepository.save(clubMember);
                });
    }
}
