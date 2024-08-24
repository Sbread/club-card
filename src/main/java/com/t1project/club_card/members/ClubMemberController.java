package com.t1project.club_card.members;

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

    @PostMapping
    public Mono<ClubMember> changeEmail(@RequestBody ClubMember newClubMember) {
        return clubMemberRepository.findMemberByUsername(currentClubMember.getUsername()).flatMap(
                clubMember -> {
                    clubMember.setEmail(newClubMember.getEmail());
                    return clubMemberRepository.save(clubMember);
                }
        );
    }
}
