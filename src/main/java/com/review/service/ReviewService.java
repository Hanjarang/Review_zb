package com.review.service;

import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> findByMovie(Movie movie) {
        // 영화에 연관된 모든 리뷰를 반환
        return reviewRepository.findByMovie(movie);
    }

}
