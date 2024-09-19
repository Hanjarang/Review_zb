package com.review.repository;

import com.review.entity.Movie;
import com.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovie(Movie movie);
    Page<Review> findByMovie(Movie movie, Pageable pageable);
}
