package com.nexoraa.interviewgen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "generation_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @Column(nullable = false)
    private String technology;

    @Column(name = "experience_level", nullable = false)
    private String experienceLevel;

    // Comma-separated categories: Technical,Coding,HR
    @Column(nullable = false)
    private String categories;

    @Column(name = "question_count")
    private Integer questionCount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Question> questions;
}
