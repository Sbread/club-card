package com.t1project.club_card;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClubMemberController {

    private final ClubMemberRepository clubMemberRepository;

    public ClubMemberController(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }


}
