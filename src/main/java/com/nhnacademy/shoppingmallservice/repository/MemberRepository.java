package com.nhnacademy.shoppingmallservice.repository;

import com.nhnacademy.shoppingmallservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByUsername(String username);
    Member findByUsername(String username);
}
