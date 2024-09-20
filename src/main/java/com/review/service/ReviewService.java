package com.review.service;

import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.exception.ReviewNotFoundException;
import com.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // 리뷰 저장
    @Transactional
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    // 영화에 연관된 리뷰 목록을 페이지 단위로 반환
    public Page<Review> findByMovie(Movie movie, Pageable pageable) {
        return reviewRepository.findByMovie(movie, pageable);
    }

    // 리뷰 ID로 리뷰 찾기
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("해당 리뷰를 찾을 수 없습니다."));
    }

    // 리뷰 삭제
    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
