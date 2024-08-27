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
public class ChangeAllUserFieldsDTO {
    private String newPassword;
    private String newFirstName;
    private String newLastName;
    private String newPhone;
    private LocalDate newBirthday;
}