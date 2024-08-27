package com.t1project.club_card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeAllUserFieldsDTO {
    private String newPassword;
    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPhone;
}