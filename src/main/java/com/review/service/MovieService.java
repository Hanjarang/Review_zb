package com.review.service;

import com.review.entity.Movie;
import com.review.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    // 영화 ID로 영화 찾기 (추가된 부분)
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    // 영화 저장
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }
}
