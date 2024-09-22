package com.review.service;

import com.review.dto.MovieDTO;
import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.repository.MovieRepository;
import com.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    // 영화 저장
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // 장편/단편 카테고리와 한글/영어 제목 구분에 따른 영화 목록 조회
//    public Page<Movie> getMoviesByCategoryAndTitle(String category, boolean isKorean, int page) {
//        int pageSize = 20; // 페이지당 최대 영화 개수
//        Sort sort = Sort.by(Sort.Order.asc("title")); // 정렬 기준
//
//        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);
//        return movieRepository.findByCategoryAndTitleMatchingFirstLetter(category, isKorean, pageRequest);
//    }
    public Page<MovieDTO> getMoviesByCategoryAndTitle(String category, boolean isKorean, int page) {
        int pageSize = 20;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Order.asc("title")));
        Page<Movie> movies = movieRepository.findByCategoryAndTitleMatchingFirstLetter(category, isKorean, pageRequest);

        return movies.map(this::convertToMovieDTO); // Convert to DTO
    }

    // 영화 제목으로 리뷰 목록을 페이지 단위로 제공
//    public List<Movie> findByTitle(String title) {
//        return movieRepository.findByTitle(title);
//    }

    public List<MovieDTO> findByTitle(String title) {
        List<Movie> movies = movieRepository.findByTitle(title);
        return movies.stream().map(this::convertToMovieDTO).toList(); // Convert to DTO
    }

    // 영화 등록 및 리뷰 작성 처리
    public ResponseEntity<String> registerMovieAndReview(String title, int releaseYear, String category,
                                                         String director, String[] cast, String plot,
                                                         String reviewContent, String username) {
        Optional<Movie> existingMovie = findByTitleAndReleaseYear(title, releaseYear);
        if (existingMovie.isPresent()) {
            // 이미 등록된 영화에 리뷰 추가
            Review review = createReview(existingMovie.get(), reviewContent, username);
            reviewRepository.save(review);
            return ResponseEntity.ok("이미 등록된 영화정보가 존재하여 리뷰만 등록됩니다. 좋은 리뷰는 좋은 영화를 만듭니다.");
        } else {
            // 새로운 영화 등록
            Movie movie = createMovie(title, releaseYear, category, director, cast, plot, username);
            saveMovie(movie);

            Review review = createReview(movie, reviewContent, username);
            reviewRepository.save(review);
            return ResponseEntity.ok("영화 정보 및 리뷰가 등록되었습니다. 좋은 리뷰는 좋은 영화를 만듭니다.");
        }
    }

    // 배우 이름으로 영화 검색
    public Page<MovieDTO> findByCastMember(String castName, int page) {
        Page<Movie> movies = movieRepository.findByCastMember(castName, PageRequest.of(page, 20));

        // Movie 엔티티를 MovieDTO로 변환
        return movies.map(movie -> new MovieDTO(
                movie.getId(), // 영화 ID
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getCategory(),
                movie.getDirector(),
                movie.getCast(), // 출연진 리스트
                movie.getPlot()
        ));
    }


    // 영화 생성 로직
    private Movie createMovie(String title, int releaseYear, String category, String director,
                              String[] cast, String plot, String username) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setReleaseYear(releaseYear);
        movie.setCategory(category);
        movie.setDirector(director);
        movie.setCast(new ArrayList<>(List.of(cast)));
        movie.setPlot(plot);
        movie.setCreatedBy(username);
        return movie;
    }

    // 리뷰 생성 로직
    private Review createReview(Movie movie, String reviewContent, String username) {
        Review review = new Review();
        review.setMovie(movie);
        review.setContent(reviewContent);
        review.setCreatedBy(username);
        return review;
    }




    private MovieDTO convertToMovieDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setCategory(movie.getCategory());
        dto.setDirector(movie.getDirector());
        dto.setCast(movie.getCast());
        dto.setPlot(movie.getPlot());
        return dto;
    }
}
