package com.t1project.club_card.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Set;

@Table("ClubMembers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMember {
    @Id
    private Integer id;
    @Column("email")
    private String email;
    @Column("password")
    private String password;
    @Column("firstName")
    private String firstName;
    @Column("lastName")
    private String lastName;
    @Column("phone")
    private String phoneNumber;
    @Column("birthday")
    private LocalDate birthday;
    @Column("privilege")
    private String privilege;
    @Column("isLocked")
    private boolean isLocked;
    @Column("role")
    private String role;
}
