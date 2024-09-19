package com.review.service;

import com.review.entity.Movie;
import com.review.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
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

        // 한글/영어 구분에 따라 검색
        if (isKorean) {
            // 한글 제목의 영화 검색 (제목의 첫 글자가 한글인지 확인)
            return movieRepository.findByCategoryAndTitleMatchingFirstLetter(category, true, pageRequest);
        } else {
            // 영어 제목의 영화 검색 (제목의 첫 글자가 영어인지 확인)
            return movieRepository.findByCategoryAndTitleMatchingFirstLetter(category, false, pageRequest);
        }
    }
}
