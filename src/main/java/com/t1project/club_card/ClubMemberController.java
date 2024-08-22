package com.t1project.club_card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/profile")
public class ClubMemberController {
    @Autowired
    ClubMemberRepository clubMemberRepository;

    ClubMemberService clubMemberService;
    ClubMember currentClubMember;

    public ClubMemberController(ClubMemberService clubMemberService) {
        this.clubMemberService = clubMemberService;
        currentClubMember = clubMemberService.getCurrentClubMember();
    }

    @GetMapping("")
    public ClubMember getCurrentClubMember() {
        return currentClubMember;
    }

    @PutMapping
    public Mono<ClubMember> changeEmail(@RequestBody String string) {
        ClubMember clubMember = clubMemberRepository.findMemberByUsername(currentClubMember.getUsername());
        clubMember.setEmail(string);
        return clubMemberRepository.save(clubMember);
    }
}
