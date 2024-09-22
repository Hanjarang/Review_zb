package com.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {
    private Long id;
    private String content;
    private String createdBy;
    private MovieDTO movie;  // 리뷰에 연결된 영화 정보를 포함하는 필드 추가
}
