package com.project.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.Member;

public interface MemberRepository2 extends JpaRepository<Member,Long>{
	
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	Member findByEmail(String email);
	Optional<Member> findByUsername(String username);
	
}
