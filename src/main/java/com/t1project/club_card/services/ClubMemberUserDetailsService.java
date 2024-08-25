package com.t1project.club_card.services;

import com.t1project.club_card.security.ClubMemberUserDetails;
import com.t1project.club_card.repositories.ClubMemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ClubMemberUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    private static final Logger logger = LoggerFactory.getLogger(ClubMemberUserDetailsService.class);

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return clubMemberRepository.findMemberByUsername(username)
                .switchIfEmpty(Mono.defer(
                        () -> {
                            logger.error("Username: {} not found", username);
                            return Mono.error(new UsernameNotFoundException("Username: " + username + " not found"));
                        }
                ))
                .doOnNext(clubMember -> logger.info("User: {} auth successfully!", username))
                .map(ClubMemberUserDetails::new);
    }
}
