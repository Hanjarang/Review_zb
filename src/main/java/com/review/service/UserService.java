package com.review.service;

import com.review.entity.User;
import com.review.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 처리
    public boolean registerUser(User user) {
        // ID 글자수 제한 확인
        if (user.getUsername().length() > 10) {
            System.out.println("Username must be 10 characters or less.");
            return false;
        }

        // 중복된 ID 확인
        if (userRepository.existsByUsername(user.getUsername())) {
            System.out.println("Username is already taken.");
            return false;
        }

        // 사용자 정보 저장
        userRepository.save(user);
        System.out.println("User registered successfully!");
        return true;
    }

    // 로그인 처리
    public User loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }

    // 회원 탈퇴 처리 (username을 기반으로 사용자 삭제)
    @Transactional
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username); // repository에서 삭제 처리
    }
}
