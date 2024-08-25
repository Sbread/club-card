package com.t1project.club_card.controllers;

import com.t1project.club_card.dto.ChangeFieldDTO;
import com.t1project.club_card.services.ClubMemberService;
import com.t1project.club_card.models.ClubMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/profile")
public class ClubMemberController {

    @Autowired
    private ClubMemberService clubMemberService;

    @GetMapping("")
    public ClubMember getCurrentClubMember(@AuthenticationPrincipal ClubMember clubMember) {
        return clubMember;
    }

    @PostMapping("/change-email")
    public Mono<ResponseEntity<String>> changeEmail(@RequestBody ChangeFieldDTO changeFieldDTO,
                                                    @AuthenticationPrincipal ClubMember clubMember) {
        return clubMemberService.changeEmail(clubMember.getUsername(), changeFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body("Email changed successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email changing failed\n" + e.getMessage())));
    }

    @PostMapping("/change-phone")
    public Mono<ResponseEntity<String>> changePhoneNumber(@RequestBody ChangeFieldDTO changeFieldDTO,
                                                          @AuthenticationPrincipal ClubMember clubMember) {
        return clubMemberService.changePhoneNumber(clubMember.getUsername(), changeFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body("PhoneNumber changed successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("PhoneNumber changing failed\n" + e.getMessage())));
    }

    @PostMapping("/change-password")
    public Mono<ResponseEntity<String>> changePassword(@RequestBody ChangeFieldDTO changeFieldDTO,
                                                          @AuthenticationPrincipal ClubMember clubMember) {
        return clubMemberService.changePassword(clubMember.getUsername(), changeFieldDTO.getNewFieldValue())
                .map(saved -> ResponseEntity.status(HttpStatus.OK).body("Password changed successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Password changing failed\n" + e.getMessage())));
    }
}
