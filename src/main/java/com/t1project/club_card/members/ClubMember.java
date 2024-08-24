package com.t1project.club_card.members;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("ClubMembers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubMember {
    @Id
    private Integer id;
    @Column("username")
    private String username;
    @Column("password")
    private String password;
    @Column("firstName")
    private String firstName;
    @Column("lastName")
    private String lastName;
    @Column("email")
    private String email;
    @Column("phoneNumber")
    private String phoneNumber;
    @Column("roles")
    private Set<ClubMemberRole> roles;
    @Column("privileges")
    private Set<ClubMemberPrivilege> privilege;
}
