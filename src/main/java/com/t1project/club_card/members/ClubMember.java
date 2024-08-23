package com.t1project.club_card.members;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table("ClubMembers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubMember implements UserDetails {
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
    @Column("role")
    private String role;
    @Column("privilege")
    private String privilege;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // need to add blocking
    }
}
