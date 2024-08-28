package com.t1project.club_card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseClubMemberDTO {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate birthDay;
    private String role;
    private String privilege;
    private boolean locked;
}
