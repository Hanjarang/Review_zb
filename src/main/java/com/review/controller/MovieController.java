package com.review.controller;

import com.review.dto.MovieDTO;
import com.review.dto.ReviewDTO;
import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.entity.User; // User 클래스 import 추가
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
    private final ForbiddenWordService forbiddenWordService;

    @Autowired
    public MovieController(MovieService movieService, ReviewService reviewService, ForbiddenWordService forbiddenWordService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.forbiddenWordService = forbiddenWordService;
    }

    @PostMapping("/review")
    public ResponseEntity<String> registerMovieAndReview(@RequestParam String title,
                                                         @RequestParam int releaseYear,
                                                         @RequestParam String category,
                                                         @RequestParam String director,
                                                         @RequestParam("cast[]") String[] cast,
                                                         @RequestParam String plot,
                                                         @RequestParam String reviewContent,
                                                         HttpServletRequest request) {
        User user = getUserFromSession(request);
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        if (cast.length > 3) {
            return ResponseEntity.badRequest().body("출연진은 최대 3명까지만 입력할 수 있습니다.");
        }

        if (forbiddenWordService.containsForbiddenWord(reviewContent)) {
            return ResponseEntity.badRequest().body("부정적인 언어의 사용으로 리뷰 등록이 불가능합니다.");
        }

        return movieService.registerMovieAndReview(title, releaseYear, category, director, cast, plot, reviewContent, user.getUsername());
    }

    private User getUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    // 영화 제목과 출시 연도로 리뷰 목록 조회
    @GetMapping("/reviews")
    public ResponseEntity<?> getReviewsForMovie(@RequestParam String title,
                                                @RequestParam(required = false) Integer releaseYear,
                                                @RequestParam(defaultValue = "0") int page) {
        if (releaseYear != null) {
            Optional<Movie> movie = movieService.findByTitleAndReleaseYear(title, releaseYear);
            if (movie.isEmpty()) {
                return ResponseEntity.badRequest().body("해당 년도에 출시된 영화가 없습니다.");
            }

            Page<ReviewDTO> reviews = reviewService.findByMovie(movie.get(), PageRequest.of(page, 20)); // Pass Movie entity
            return ResponseEntity.ok(reviews);
        }

        List<MovieDTO> movieDTOs = movieService.findByTitle(title); // Get MovieDTOs

        if (movieDTOs.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 제목의 영화로 등록된 리뷰가 없습니다.");
        } else if (movieDTOs.size() > 1) {
            return ResponseEntity.badRequest().body("동일 제목의 영화가 있습니다. 검색을 희망하는 영화의 년도를 입력하세요.");
        }

        // Fetch Movie entity again based on MovieDTO info
        Optional<Movie> movie = movieService.findByTitleAndReleaseYear(movieDTOs.get(0).getTitle(), movieDTOs.get(0).getReleaseYear());
        if (movie.isEmpty()) {
            return ResponseEntity.badRequest().body("영화 정보를 찾을 수 없습니다.");
        }

        Page<ReviewDTO> reviews = reviewService.findByMovie(movie.get(), PageRequest.of(page, 20)); // Pass Movie entity
        return ResponseEntity.ok(reviews);
    }

    // 영화 목록 조회 (카테고리와 제목 종류로 구분)
    @GetMapping("/list")
//    public ResponseEntity<Page<Movie>> getMovies(@RequestParam String category,
//                                                 @RequestParam boolean isKorean,
//                                                 @RequestParam int page) {
//        return ResponseEntity.ok(movieService.getMoviesByCategoryAndTitle(category, isKorean, page));
//    }
    public ResponseEntity<Page<MovieDTO>> getMovies(@RequestParam String category,
                                                    @RequestParam boolean isKorean,
                                                    @RequestParam int page) {
        return ResponseEntity.ok(movieService.getMoviesByCategoryAndTitle(category, isKorean, page));
    }

    // 출연진 이름으로 영화 목록 조회
    @GetMapping("/cast-search")
    public ResponseEntity<?> getMoviesByCast(
            @RequestParam String castName,
            @RequestParam(defaultValue = "0") int page) {

        Page<MovieDTO> movies = movieService.findByCastMember(castName, page);

        if (movies.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 배우의 이름으로 등록된 영화가 없습니다.");
        }

        return ResponseEntity.ok(movies);
    }

    // 리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId,
                                               @RequestParam String content,
                                               HttpServletRequest request) {
        User user = getUserFromSession(request);
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

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

        // 리뷰 내용 수정
        review.setContent(content);
        reviewService.saveReview(review);

        return ResponseEntity.ok("리뷰가 수정되었습니다.");
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               HttpServletRequest request) {
        User user = getUserFromSession(request);
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

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
                                            @RequestParam("cast[]") String[] cast,
                                            @RequestParam String plot,
                                            HttpServletRequest request) {
        User user = getUserFromSession(request);
        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 관리자 권한 확인
        if (!"ADMIN".equals(user.getRole())) {
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
}