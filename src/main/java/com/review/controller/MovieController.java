package com.review.controller;

import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.entity.User;  // User 클래스 import 추가
import com.review.service.ForbiddenWordService;
import com.review.service.MovieService;
import com.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final ForbiddenWordService forbiddenWordService;  // ForbiddenWordService 추가

    @Autowired
    public MovieController(MovieService movieService, ReviewService reviewService, ForbiddenWordService forbiddenWordService) {  // ForbiddenWordService 주입
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.forbiddenWordService = forbiddenWordService;  // 주입된 ForbiddenWordService 초기화
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

        // 출연진 수 제한 (3명까지만 허용)
        if (cast.length > 3) {
            return ResponseEntity.badRequest().body("출연진은 최대 3명까지만 입력할 수 있습니다.");
        }

        // 세션에서 User 객체를 가져옵니다.
        User user = (User) session.getAttribute("user");

        // 리뷰 내용에 검열 단어가 포함되어 있는지 확인
        if (forbiddenWordService.containsForbiddenWord(reviewContent)) {
            return ResponseEntity.badRequest().body("부정적인 언어의 사용으로 리뷰 등록이 불가능합니다.");
        }

        // 영화 정보 확인
        Optional<Movie> existingMovie = movieService.findByTitleAndReleaseYear(title, releaseYear);
        if (existingMovie.isPresent()) {
            // 이미 영화 정보가 존재하는 경우, 리뷰만 등록
            Review review = new Review();
            review.setMovie(existingMovie.get());
            review.setContent(reviewContent);
            review.setCreatedBy(user.getUsername());  // User 객체에서 username을 사용
            reviewService.saveReview(review);

            return ResponseEntity.ok("이미 등록된 영화정보가 존재하여 리뷰만 등록됩니다. 좋은 리뷰는 좋은 영화를 만듭니다.");
        } else {
            // 영화 정보가 존재하지 않는 경우 영화 정보 등록 및 리뷰 작성
            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setReleaseYear(releaseYear);
            movie.setCategory(category);
            movie.setDirector(director);
            movie.setCast(new ArrayList<>(List.of(cast)));  // mutable 리스트로 설정
            movie.setPlot(plot);
            movie.setCreatedBy(user.getUsername());  // User 객체에서 username을 사용

            movieService.saveMovie(movie);

            // 리뷰 작성
            Review review = new Review();
            review.setMovie(movie);
            review.setContent(reviewContent);
            review.setCreatedBy(user.getUsername());  // User 객체에서 username을 사용

            reviewService.saveReview(review);

            return ResponseEntity.ok("영화 정보 및 리뷰가 등록되었습니다. 좋은 리뷰는 좋은 영화를 만듭니다.");
        }
    }


    // 영화 제목과 출시 연도로 리뷰 목록 조회
// 영화 제목과 출시 연도로 리뷰 목록 조회
    @GetMapping("/reviews")
    public ResponseEntity<?> getReviewsForMovie(
            @RequestParam String title,
            @RequestParam(required = false) Integer releaseYear, // 년도는 선택 입력
            @RequestParam(defaultValue = "0") int page) { // 페이지 번호 기본값 설정

        // 제목과 출시 연도를 모두 입력했을 경우
        if (releaseYear != null) {
            Optional<Movie> movie = movieService.findByTitleAndReleaseYear(title, releaseYear);
            if (movie.isEmpty()) {
                return ResponseEntity.badRequest().body("해당 년도에 출시된 영화가 없습니다.");
            }

            // 영화와 연관된 리뷰를 20개씩 페이지 단위로 반환
            Page<Review> reviews = reviewService.findByMovie(movie.get(), PageRequest.of(page, 20));
            return ResponseEntity.ok(reviews);
        }

        // 제목만 입력했을 경우
        List<Movie> movies = movieService.findByTitle(title);

        if (movies.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 제목의 영화로 등록된 리뷰가 없습니다.");
        } else if (movies.size() > 1) {
            // 동일 제목의 영화가 여러 개일 경우
            return ResponseEntity.badRequest().body("동일 제목의 영화가 있습니다. 검색을 희망하는 영화의 년도를 입력하세요.");
        }

        // 동일 제목의 영화가 하나일 경우
        Movie movie = movies.get(0);
        Page<Review> reviews = reviewService.findByMovie(movie, PageRequest.of(page, 20));
        return ResponseEntity.ok(reviews);
    }



    // 리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId,
                                               @RequestParam String content,
                                               HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 세션에서 User 객체를 가져옵니다.
        User user = (User) session.getAttribute("user");

        // 리뷰를 ID로 검색
        Optional<Review> existingReview = reviewService.findById(reviewId);
        if (existingReview.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 리뷰를 찾을 수 없습니다.");
        }

        Review review = existingReview.get();

        // 리뷰 작성자와 현재 사용자의 username이 일치하는지 확인
        if (!review.getCreatedBy().equals(user.getUsername())) {
            return ResponseEntity.status(403).body("수정할 권한이 없습니다.");
        }

        // 리뷰 내용 수정 (영화 정보는 수정 불가)
        review.setContent(content);
        reviewService.saveReview(review);

        return ResponseEntity.ok("리뷰가 수정되었습니다.");
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 세션에서 User 객체를 가져옵니다.
        User user = (User) session.getAttribute("user");

        // 리뷰를 ID로 검색
        Optional<Review> existingReview = reviewService.findById(reviewId);
        if (existingReview.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 리뷰를 찾을 수 없습니다.");
        }

        Review review = existingReview.get();

        // 리뷰 작성자와 현재 사용자의 username이 일치하는지 확인
        if (!review.getCreatedBy().equals(user.getUsername())) {
            return ResponseEntity.status(403).body("삭제할 권한이 없습니다.");
        }

        // 리뷰 삭제
        reviewService.deleteReviewById(reviewId);

        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    // 관리자 권한 리뷰 수정
    @PutMapping("/edit")
    public ResponseEntity<String> editMovie(@RequestParam String title,
                                            @RequestParam int releaseYear,
                                            @RequestParam String category,
                                            @RequestParam String director,
                                            @RequestParam("cast[]") String[] cast,  // 출연진 배열
                                            @RequestParam String plot,
                                            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 세션에서 User 객체를 가져옵니다.
        User user = (User) session.getAttribute("user");

        // 관리자 권한 확인
        if (!"ADMIN".equals(user.getRole())) {  // "ADMIN" 권한 확인
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }

        // 영화 제목과 출시년도로 영화 찾기
        Optional<Movie> movie = movieService.findByTitleAndReleaseYear(title, releaseYear);
        if (movie.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 영화 정보를 찾을 수 없습니다.");
        }

        Movie existingMovie = movie.get();

        // 영화 정보 수정
        existingMovie.setTitle(title);
        existingMovie.setReleaseYear(releaseYear);
        existingMovie.setCategory(category);
        existingMovie.setDirector(director);
        existingMovie.setCast(new ArrayList<>(List.of(cast)));
        existingMovie.setPlot(plot);

        movieService.saveMovie(existingMovie);

        return ResponseEntity.ok("관리자의 권한으로 영화 정보가 수정되었습니다.");
    }

    // 영화 목록 조회 (카테고리와 제목 종류로 구분)
    @GetMapping("/list")
    public ResponseEntity<Page<Movie>> getMovies(
            @RequestParam String category,  // "장편" 또는 "단편"
            @RequestParam boolean isKorean, // true면 한글 제목, false면 영어 제목
            @RequestParam int page) {       // 페이지 번호

        Page<Movie> movies = movieService.getMoviesByCategoryAndTitle(category, isKorean, page);
        return ResponseEntity.ok(movies);
    }




}
