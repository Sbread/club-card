package com.t1project.club_card;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ClubMemberService {
    public ClubMember getCurrentClubMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof ClubMember) {
                return (ClubMember) principal; // Cast and return the Member object
            }
        }
        return null;
    }
}
