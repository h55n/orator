package com.nexoraa.interviewgen.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GenerationSession session;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT")
    private String hint;

    @Column(nullable = false)
    private String category;      // Technical | Coding | SQL | HR | Aptitude

    @Column(nullable = false)
    private String difficulty;    // Easy | Medium | Hard

    @Column(name = "job_role")
    private String jobRole;

    @Column
    private String technology;

    @Column(name = "experience_level")
    private String experienceLevel;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
