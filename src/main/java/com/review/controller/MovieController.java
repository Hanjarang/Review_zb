package com.review.controller;

import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.entity.User;  // User 클래스 import 추가
import com.review.service.MovieService;
import com.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final ReviewService reviewService;

    @Autowired
    public MovieController(MovieService movieService, ReviewService reviewService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
    }

    // 영화 정보 등록 및 리뷰 작성 (최초 등록 시)
    @PostMapping("/review")
    public ResponseEntity<String> registerMovieAndReview(@RequestParam String title,
                                                         @RequestParam int releaseYear,
                                                         @RequestParam String category,
                                                         @RequestParam String director,
                                                         @RequestParam("cast[]") String[] cast,  // 출연진 배열
                                                         @RequestParam String plot,
                                                         @RequestParam String reviewContent,
                                                         HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 세션에서 User 객체를 가져옵니다.
        User user = (User) session.getAttribute("user");

        // 영화 정보 확인
        Optional<Movie> existingMovie = movieService.findByTitleAndReleaseYear(title, releaseYear);
        if (existingMovie.isPresent()) {
            // 이미 영화 정보가 존재하는 경우, 리뷰만 등록
            Review review = new Review();
            review.setMovie(existingMovie.get());
            review.setContent(reviewContent);
            review.setCreatedBy(user.getUsername());  // User 객체에서 username을 사용
            reviewService.saveReview(review);

            return ResponseEntity.ok("이미 등록된 영화정보가 존재합니다. 좋은 리뷰는 좋은 영화를 만듭니다.");
        } else {
            // 영화 정보가 존재하지 않는 경우 영화 정보 등록 및 리뷰 작성
            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setReleaseYear(releaseYear);
            movie.setCategory(category);
            movie.setDirector(director);
            movie.setCast(List.of(cast));  // 배열을 리스트로 변환
            movie.setPlot(plot);
            movie.setCreatedBy(user.getUsername());  // User 객체에서 username을 사용

            movieService.saveMovie(movie);

            // 리뷰 작성
            Review review = new Review();
            review.setMovie(movie);
            review.setContent(reviewContent);
            review.setCreatedBy(user.getUsername());  // User 객체에서 username을 사용

            reviewService.saveReview(review);

            return ResponseEntity.ok("영화 정보 및 리뷰가 등록되었습니다.좋은 리뷰는 좋은 영화를 만듭니다.");
        }
    }


    // 영화 리뷰 목록 조회
    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getReviewsForMovie(@RequestParam String title, @RequestParam int releaseYear) {
        Optional<Movie> movie = movieService.findByTitleAndReleaseYear(title, releaseYear);
        if (movie.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // 영화와 연관된 모든 리뷰 가져오기
        List<Review> reviews = reviewService.findByMovie(movie.get());
        return ResponseEntity.ok(reviews);
    }



}
