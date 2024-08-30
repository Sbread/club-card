package com.t1project.club_card.services;

import com.t1project.club_card.dto.ChangeAllUserFieldsDTO;
import com.t1project.club_card.utils.Utils;
import com.t1project.club_card.dto.RegisterRequestDTO;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.repositories.ClubMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public Flux<ClubMember> findAllPaged(int page, int size, String email) {
        return clubMemberRepository.findAllByEmailNotAndEmailNot(PageRequest.of(page, size), email, "superuser@yandex.ru");
    }

    public Mono<Long> countAll() {
        return clubMemberRepository.count();
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
                .template("1")
                .build();
        System.out.println(clubMember.toString());
        return clubMemberRepository.save(clubMember);
    }

    @Transactional
    public Mono<ClubMember> changeAllFields(String email, ChangeAllUserFieldsDTO fields) {
        return clubMemberRepository.findByEmail(fields.getEmail())
                .flatMap(clubMember -> {
                    clubMember.setEmail(fields.getEmail());
                    if (fields.getPassword() != null) {
                        clubMember.setPassword(Utils.bCryptPasswordEncoder.encode(fields.getPassword()));
                    }
                    clubMember.setFirstName(fields.getFirstName());
                    clubMember.setLastName(fields.getLastName());
                    clubMember.setPhoneNumber(fields.getPhone());
                    clubMember.setBirthday(fields.getBirthday());
                    return clubMemberRepository.save(clubMember);
                });
    }
}
