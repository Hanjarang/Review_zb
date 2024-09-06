package com.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;  // 리뷰 대상 영화

    @Column(nullable = false, length = 300)
    private String content;  // 리뷰 내용 (300자 제한)

    @Column(nullable = false)
    private String createdBy;  // 리뷰 작성자 (username)


}
