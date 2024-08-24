package com.t1project.club_card.members;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClubMemberUserDetails extends ClubMember implements UserDetails {

    private final String username;
    private final String password;
    Collection<? extends GrantedAuthority> authorities;

    public ClubMemberUserDetails(ClubMember clubMember) {
        this.username = clubMember.getUsername();
        this.password = clubMember.getPassword();
        List<GrantedAuthority> auth = new ArrayList<>();
        for (ClubMemberRole clubMemberRole : clubMember.getRoles()) {
            auth.add(new SimpleGrantedAuthority(clubMemberRole.getRole().toUpperCase()));
        }
        this.authorities = auth;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
