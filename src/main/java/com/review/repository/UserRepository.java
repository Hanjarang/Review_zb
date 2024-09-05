package com.review.repository;

import com.review.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);  // ID로 사용자 검색

    boolean existsByUsername(String username);  // ID 중복 확인

    void deleteByUsername(String username); // 특정 username으로 사용자 삭제
}
