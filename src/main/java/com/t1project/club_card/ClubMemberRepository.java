package com.t1project.club_card;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ClubMemberRepository extends ReactiveCrudRepository<ClubMember, Integer> {
    ClubMember findMemberByUsername(String username);
}
