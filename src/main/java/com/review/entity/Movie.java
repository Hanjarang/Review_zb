package com.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity (name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;  // 영화 제목

    @Column(nullable = false)
    private int releaseYear;  // 출시년도

    @Column(nullable = false)
    private String category;  // 카테고리

    @Column(nullable = false)
    private String director;  // 감독

    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "cast_name")
    private List<String> cast = new ArrayList<>();  // 출연진 (최대 3명), ArrayList로 초기화

    @Column(nullable = false, length = 1000)
    private String plot;  // 줄거리

    @Column(nullable = false)
    private String createdBy;  // 최초 등록자

}