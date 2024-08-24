package com.t1project.club_card.members;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClubMemberRepository extends ReactiveCrudRepository<ClubMember, Integer> {
    Mono<ClubMember> findMemberByUsername(String username);
}
