package com.t1project.club_card.repositories;

import com.t1project.club_card.models.ClubMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Pageable;


@Repository
public interface ClubMemberRepository extends ReactiveCrudRepository<ClubMember, Integer> {
    Flux<ClubMember> findAllBy(Pageable pageable);

    Mono<ClubMember> findByEmail(String email);
}
