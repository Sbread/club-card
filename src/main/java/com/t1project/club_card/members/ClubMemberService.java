package com.t1project.club_card.members;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ClubMemberService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

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

    public Mono<ClubMember> registerClubMember(ClubMember clubMember) {
        return clubMemberRepository.save(clubMember);
    }
}
