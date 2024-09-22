package com.review.repository;

import com.review.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitleAndReleaseYear(String title, int releaseYear);

    // 장편/단편 구분 및 제목의 첫 글자가 한글 또는 영어로 시작하는 영화 목록을 페이지 형식으로 가져옴
    @Query("SELECT m FROM movies m WHERE m.category = :category AND " +
            "((:isKorean = true AND SUBSTRING(m.title, 1, 1) BETWEEN '가' AND '힣') OR " +
            " (:isKorean = false AND SUBSTRING(m.title, 1, 1) BETWEEN 'A' AND 'Z'))")
    Page<Movie> findByCategoryAndTitleMatchingFirstLetter(String category, boolean isKorean, Pageable pageable);

    // 영화 제목으로 영화 검색
    List<Movie> findByTitle(String title);

    // 출연진 이름으로 영화 검색
    Page<Movie> findByCastContaining(String castMember, Pageable pageable);

}
