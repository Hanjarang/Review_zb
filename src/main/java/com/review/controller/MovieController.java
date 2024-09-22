package com.review.controller;

import com.review.entity.Movie;
import com.review.entity.Review;
import com.review.entity.User;
import com.review.exception.MovieNotFoundException;
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

        Movie movie;
        boolean isNewMovie = false;
        try {
            // 영화 정보 확인 (이미 등록된 영화인지 확인)
            movie = movieService.findByTitleAndReleaseYear(title, releaseYear);
        } catch (MovieNotFoundException e) {
            // 영화 정보가 없을 경우 새로 등록
            movie = new Movie();
            movie.setTitle(title);
            movie.setReleaseYear(releaseYear);
            movie.setCategory(category);
            movie.setDirector(director);
            movie.setCast(new ArrayList<>(List.of(cast)));  // mutable 리스트로 설정
            movie.setPlot(plot);
            movie.setCreatedBy(user.getUsername());

            movieService.saveMovie(movie);
            isNewMovie = true; // 새로운 영화가 등록되었음을 표시
        }

        // 리뷰 작성
        Review review = new Review();
        review.setMovie(movie);
        review.setContent(reviewContent);
        review.setCreatedBy(user.getUsername());  // User 객체에서 username을 사용
        reviewService.saveReview(review);

        // 영화 정보가 새로 등록되었는지 여부에 따라 다른 메시지 반환
        if (isNewMovie) {
            return ResponseEntity.ok("영화 정보 및 리뷰가 등록되었습니다. 좋은 리뷰는 좋은 영화를 만듭니다.");
        } else {
            return ResponseEntity.ok("이미 등록된 영화정보가 존재하여 리뷰만 등록됩니다. 좋은 리뷰는 좋은 영화를 만듭니다.");
        }
    }





    // 영화 제목과 출시 연도로 리뷰 목록 조회
    @GetMapping("/reviews")
    public ResponseEntity<?> getReviewsForMovie(@RequestParam String title,
                                                @RequestParam(required = false) Integer releaseYear,
                                                @RequestParam(defaultValue = "0") int page) {
        Movie movie;
        if (releaseYear != null) {
            movie = movieService.findByTitleAndReleaseYear(title, releaseYear);
        } else {
            List<Movie> movies = movieService.findByTitle(title);
            if (movies.size() > 1) {
                return ResponseEntity.badRequest().body("동일 제목의 영화가 있습니다. 년도를 입력하세요.");
            }
            movie = movies.get(0);
        }

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

        User user = (User) session.getAttribute("user");

        Review review = reviewService.findById(reviewId);
        if (!review.getCreatedBy().equals(user.getUsername())) {
            return ResponseEntity.status(403).body("수정할 권한이 없습니다.");
        }

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

        User user = (User) session.getAttribute("user");

        Review review = reviewService.findById(reviewId);
        if (!review.getCreatedBy().equals(user.getUsername())) {
            return ResponseEntity.status(403).body("삭제할 권한이 없습니다.");
        }

        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }

    // 영화 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<Movie>> getMovies(@RequestParam String category,
                                                 @RequestParam boolean isKorean,
                                                 @RequestParam int page) {
        Page<Movie> movies = movieService.getMoviesByCategoryAndTitle(category, isKorean, page);
        return ResponseEntity.ok(movies);
    }

    // 출연진 이름으로 영화 목록 조회
    @GetMapping("/cast-search")
    public ResponseEntity<Page<Movie>> getMoviesByCast(
            @RequestParam String castName,
            @RequestParam(defaultValue = "0") int page) {

        Page<Movie> movies = movieService.findByCastMember(castName, page);
        return ResponseEntity.ok(movies);
    }
}

