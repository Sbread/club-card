package com.t1project.club_card.security;

import com.t1project.club_card.models.ClubMember;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClubMemberUserDetails extends ClubMember implements UserDetails {

    private final String username;
    private final String password;
    private final boolean isLocked;
    Collection<? extends GrantedAuthority> authorities;

    public ClubMemberUserDetails(ClubMember clubMember) {
        this.username = clubMember.getUsername();
        this.password = clubMember.getPassword();
        this.isLocked = clubMember.isLocked();
        List<GrantedAuthority> auth = new ArrayList<>();
        for (String clubMemberRole : clubMember.getRoles()) {
            auth.add(new SimpleGrantedAuthority(clubMemberRole));
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
        return !isLocked;
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
