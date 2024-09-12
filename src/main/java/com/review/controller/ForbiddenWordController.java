package com.review.controller;

import com.review.entity.ForbiddenWord;
import com.review.entity.User;
import com.review.service.ForbiddenWordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forbidden-words")
public class ForbiddenWordController {

    private final ForbiddenWordService forbiddenWordService;

    @Autowired
    public ForbiddenWordController(ForbiddenWordService forbiddenWordService) {
        this.forbiddenWordService = forbiddenWordService;
    }

    //  검열 단어 목록 조회
    @GetMapping
    public ResponseEntity<List<String>> getForbiddenWords(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body(null);
        }

        // 세션에서 User 객체를 가져옵니다.
        User user = (User) session.getAttribute("user");

        // 관리자 권한 확인
        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body(null);
        }

        List<ForbiddenWord> words = forbiddenWordService.getAllForbiddenWords();
        List<String> wordList = words.stream().map(ForbiddenWord::getWord).toList();
        return ResponseEntity.ok(wordList);
    }


    // 검열 단어 추가
    @PostMapping
    public ResponseEntity<String> addForbiddenWord(@RequestParam String word, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("관리자 권한 로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");
        // 관리자 확인
        if(!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }

        // 단어 중복 체크
        if(forbiddenWordService.containsForbiddenWord(word)){
            return ResponseEntity.badRequest().body("이미 존재하는 단어입니다.");
        }

        forbiddenWordService.addForbiddenWord(word);
        return ResponseEntity.ok("검열단어가 추가되었습니다.");
    }


    // 검열 단어 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteForbiddenWord(@RequestParam String word, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("관리자 권한 로그인이 필요합니다.");
        }

        User user = (User) session.getAttribute("user");
        // 관리자 확인
        if(!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }

        forbiddenWordService.deleteForbiddenWord(word);
        return ResponseEntity.ok("검열 단어가 삭제되었습니다.");
    }
}
