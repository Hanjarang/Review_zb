package com.review.controller;

import com.review.entity.User;
import com.review.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 처리
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (userService.registerUser(user)) {
            return ResponseEntity.ok("회원가입이 완료되었습니다. 좋은 리뷰 공간을 만들어주세요.");
        } else {
            return ResponseEntity.badRequest().body("Registration failed. Username might be taken or invalid.");
        }
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            // 로그인 성공 시 세션에 사용자 정보 저장
            HttpSession session = request.getSession();  // 세션이 없으면 새로 생성
            session.setAttribute("user", user);  // 세션에 사용자 정보 저장
            return ResponseEntity.ok("로그인 되었습니다.");
        } else {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);  // 현재 세션 가져오기 (있으면)
        if (session != null) {
            session.invalidate();  // 세션 무효화
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 회원 탈퇴 처리
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);  // 현재 세션 가져오기 (있으면)
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                userService.deleteUser(user.getUsername());  // 서비스에서 사용자 삭제
                session.invalidate();  // 세션 무효화
                return ResponseEntity.ok("회원 탈퇴가 완료되었습니다. 영화가 궁금할 때 언제든 다시 찾아주세요.");
            }
        }
        return ResponseEntity.status(401).body("탈퇴 실패: 로그인 상태를 확인해주세요.");
    }
}
