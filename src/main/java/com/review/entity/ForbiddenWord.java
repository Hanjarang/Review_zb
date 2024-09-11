package com.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "forbidden_words")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForbiddenWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false,unique = true) // 공백값, 중복값 없도록 설정
    private String word; // 검열 단어
}
