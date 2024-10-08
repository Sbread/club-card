package com.t1project.club_card.security;

import com.t1project.club_card.services.ClubMemberUserDetailsService;
import com.t1project.club_card.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.security.auth.login.CredentialException;

@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private ClubMemberUserDetailsService clubMemberUserDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();
        return clubMemberUserDetailsService.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User: " + username + " not found")))
                .flatMap(userDetails -> {
                    if (Utils.bCryptPasswordEncoder.matches(rawPassword, userDetails.getPassword())) {
                        UsernamePasswordAuthenticationToken authenticationToken
                                = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        return Mono.just(authenticationToken);
                    } else {
                        return Mono.error(new CredentialException("Invalid credential"));
                    }
                });
    }
}
