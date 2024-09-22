package com.review.service;

import com.review.dto.ReviewDTO;
import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public List<Review> findByMovie(Movie movie) {
        // 영화에 연관된 모든 리뷰를 반환
        return reviewRepository.findByMovie(movie);
    }

    // 리뷰 ID로 리뷰 찾기
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    // 리뷰 삭제
    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }

    // 영화에 대한 리뷰를 페이징하여 반환하는 메서드 추가
//    public Page<Review> findByMovie(Movie movie, Pageable pageable) {
//        return reviewRepository.findByMovie(movie, pageable);
//    }

    public Page<ReviewDTO> findByMovie(Movie movie, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByMovie(movie, pageable);
        return reviews.map(this::convertToReviewDTO); // Convert to DTO
    }

    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setContent(review.getContent());
        dto.setCreatedBy(review.getCreatedBy());
        return dto;
    }

}
