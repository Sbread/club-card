package com.t1project.club_card.utils;

import com.t1project.club_card.dto.JwtResponseDTO;
import com.t1project.club_card.dto.MembersPageDTO;
import com.t1project.club_card.dto.ResponseClubMemberDTO;
import com.t1project.club_card.dto.RoleTemplatesResponseDTO;
import com.t1project.club_card.models.ClubMember;
import com.t1project.club_card.models.RoleCardTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.StreamSupport;

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

    public static JwtResponseDTO mapToJwtResponse(String accessToken) {
        return JwtResponseDTO.builder().accessToken(accessToken).build();
    }

    public static ResponseClubMemberDTO mapToResponseDTO(ClubMember member) {
        return ResponseClubMemberDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .phone(member.getPhoneNumber())
                .birthDay(member.getBirthday())
                .role(member.getRole())
                .privilege(member.getPrivilege())
                .locked(member.isLocked())
                .build();
    }

    public static MembersPageDTO mapToPageResponseDTO(Iterable<ClubMember> result,
                                                      long total) {
        final List<ResponseClubMemberDTO> membersDTOs = StreamSupport
                .stream(result.spliterator(), false)
                .map(Utils::mapToResponseDTO)
                .toList();
        return MembersPageDTO.builder()
                .result(membersDTOs)
                .total(total)
                .build();
    }

    public static RoleTemplatesResponseDTO mapToRoleTemplatesResponseDTO(RoleCardTemplate roleCardTemplate) {
        return RoleTemplatesResponseDTO.builder()
                .role(roleCardTemplate.getRole())
                .templates(roleCardTemplate.getTemplates())
                .build();
    }
}
