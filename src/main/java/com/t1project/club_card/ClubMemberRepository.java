package com.t1project.club_card;

import org.springframework.data.repository.CrudRepository;

public interface ClubMemberRepository extends CrudRepository<ClubMember, Integer> {
    ClubMember findMemberByUsername(String username);
}
