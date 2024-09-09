package com.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 자동 생성되는 고유 ID

    @NotBlank(message = "Username is mandatory")
    @Size(max = 10, message = "Username must be 10 characters or less")
    @Column(unique = true, nullable = false)
    private String username;  // 사용자 ID

    @NotBlank(message = "Name is mandatory")
    private String name;  // 사용자 이름

    @NotBlank(message = "Phone number is mandatory")
    private String phone;  // 사용자 전화번호

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;  // 사용자 이메일

    @NotBlank(message = "Password is mandatory")
    private String password;  // 사용자 비밀번호

    @Column(nullable = false)
    private String role = "USER"; // 별도의 입력이 없을 경우 기본값 USER

}
