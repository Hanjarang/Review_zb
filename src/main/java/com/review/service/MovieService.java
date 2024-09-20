package com.review.service;

import com.review.entity.Movie;
import com.review.exception.MovieNotFoundException;
import com.review.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // 제목과 출시년도로 영화 찾기
    public Movie findByTitleAndReleaseYear(String title, int releaseYear) {
        return movieRepository.findByTitleAndReleaseYear(title, releaseYear)
                .orElseThrow(() -> new MovieNotFoundException("해당 년도에 출시된 영화가 없습니다."));
    }

    // 영화 제목으로 영화 찾기
    public List<Movie> findByTitle(String title) {
        List<Movie> movies = movieRepository.findByTitle(title);
        if (movies.isEmpty()) {
            throw new MovieNotFoundException("해당 제목의 영화가 없습니다.");
        }
        return movies;
    }

    // 영화 저장
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // 카테고리와 제목 종류로 영화 목록 조회
    public Page<Movie> getMoviesByCategoryAndTitle(String category, boolean isKorean, int page) {
        int pageSize = 20;
        Sort sort = Sort.by(Sort.Order.asc("title"));
        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);
        return movieRepository.findByCategoryAndTitleMatchingFirstLetter(category, isKorean, pageRequest);
    }

    // 출연진 이름으로 영화 검색 (한 페이지당 20개씩 반환)
    public Page<Movie> findByCastMember(String castMember, int page) {
        PageRequest pageRequest = PageRequest.of(page, 20); // 페이지 크기 20으로 설정
        return movieRepository.findByCastContaining(castMember, pageRequest);
    }

}
