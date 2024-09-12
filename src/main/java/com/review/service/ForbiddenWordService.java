package com.review.service;

import com.review.entity.ForbiddenWord;
import com.review.repository.ForbiddenWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ForbiddenWordService {

    private final ForbiddenWordRepository forbiddenWordRepository;

    @Autowired
    public ForbiddenWordService(ForbiddenWordRepository forbiddenWordRepository) {
        this.forbiddenWordRepository = forbiddenWordRepository;
    }

    // 모든 검열단어 조회
    public List<ForbiddenWord> getAllForbiddenWords() {
        return forbiddenWordRepository.findAll();
    }

    // 검열단어 추가
    public ForbiddenWord addForbiddenWord(String word) {
        ForbiddenWord forbiddenWord = new ForbiddenWord();
        forbiddenWord.setWord(word);
        return forbiddenWordRepository.save(forbiddenWord);
    }

    // 검열 단어 삭제
    public void deleteForbiddenWord(String word) {
        Optional<ForbiddenWord> forbiddenWord = forbiddenWordRepository.findByWord(word);
        // 단어가 존재하면 삭제
        if (forbiddenWord.isPresent()) {
            forbiddenWordRepository.delete(forbiddenWord.get());
        }
    }

    // 리뷰 내용 검열단어 포함했는가 확인
    public boolean containsForbiddenWord(String content) {
        List<ForbiddenWord> forbiddenWords = getAllForbiddenWords();
        for(ForbiddenWord forbiddenWord : forbiddenWords) {
            if(content.contains(forbiddenWord.getWord())) {
                return true;
            }
        }
        return false;
    }



}
