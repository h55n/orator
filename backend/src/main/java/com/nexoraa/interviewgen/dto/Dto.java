package com.nexoraa.interviewgen.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class Dto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        private String name;
        @NotBlank @Email(message = "Valid email required")
        private String email;
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank @Email private String email;
        @NotBlank         private String password;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String  token;
        private UserDto user;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserDto {
        private Long   id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class GenerateRequest {
        @NotBlank(message = "Job role is required")
        private String jobRole;
        @NotBlank(message = "Technology is required")
        private String technology;
        @NotBlank
        @Pattern(regexp = "Fresher|Intermediate|Experienced",
                 message = "Must be Fresher, Intermediate, or Experienced")
        private String experienceLevel;
        @NotEmpty(message = "Select at least one category")
        private List<String> categories;
        @Min(5) @Max(30)
        private int questionCount = 10;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GenerateResponse {
        private Long             sessionId;
        private List<QuestionDto> questions;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class QuestionDto {
        private Long    id;
        private String  question;
        private String  hint;
        private String  category;
        private String  difficulty;
        private String  technology;
        private String  jobRole;
        private String  experienceLevel;
        private boolean isFavorite;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SessionDto {
        private Long             id;
        private String           jobRole;
        private String           technology;
        private String           experienceLevel;
        private List<String>     categories;
        private Integer          questionCount;
        private LocalDateTime    createdAt;
        private List<QuestionDto> questions;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SessionSummaryDto {
        private Long             id;
        private String           jobRole;
        private String           technology;
        private String           experienceLevel;
        private List<String>     categories;
        private Integer          questionCount;
        private LocalDateTime    createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ApiError {
        private int    status;
        private String message;
        private LocalDateTime timestamp;
    }
}
