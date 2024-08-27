package com.t1project.club_card.utils;

import com.t1project.club_card.dto.ResponseClubMemberDTO;
import com.t1project.club_card.models.ClubMember;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class Utils {
    private Utils() {
    }

    public static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    public static ResponseClubMemberDTO mapToResponseDTO(ClubMember member) {
        return ResponseClubMemberDTO.builder()
                .email(member.getEmail())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .phone(member.getPhoneNumber())
                .birthDay(member.getBirthday())
                .locked(member.isLocked())
                .build();
    }
}
