package com.review.service;

import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.repository.MovieRepository;
import com.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository, ReviewRepository reviewRepository) {
        this.movieRepository = movieRepository;
        this.reviewRepository = reviewRepository;
    }

    // 제목과 출시년도로 영화 찾기
    public Optional<Movie> findByTitleAndReleaseYear(String title, int releaseYear) {
        return movieRepository.findByTitleAndReleaseYear(title, releaseYear);
    }


//    // 영화 ID로 영화 찾기 (추가된 부분)
//    public Optional<Movie> findById(Long id) {
//        return movieRepository.findById(id);
//    }

    // 영화 저장
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // 장편/단편 카테고리와 한글/영어 제목 구분에 따른 영화 목록 조회
    public Page<Movie> getMoviesByCategoryAndTitle(String category, boolean isKorean, int page) {
        int pageSize = 20; // 페이지당 최대 영화 개수
        Sort sort = Sort.by(Sort.Order.asc("title")); // 정렬 기준 (한글/영어 모두 제목 순 정렬)

        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);

        // isKorean 값을 그대로 repository로 전달
        return movieRepository.findByCategoryAndTitleMatchingFirstLetter(category, isKorean, pageRequest);
    }


    // 영화 제목으로 리뷰 목록을 페이지 단위로 제공
    public List<Movie> findByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

}
