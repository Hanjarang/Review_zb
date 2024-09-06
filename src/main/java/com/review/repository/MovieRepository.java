package com.review.repository;

import com.review.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitleAndReleaseYear(String title, int releaseYear);
}
