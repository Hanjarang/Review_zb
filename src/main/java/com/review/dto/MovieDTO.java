package com.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long id;  // 영화 식별자 필드 추가
    private String title;
    private int releaseYear;
    private String category;
    private String director;
    private List<String> cast;
    private String plot;
}