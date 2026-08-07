package com.nexoraa.interviewgen.repository;

import com.nexoraa.interviewgen.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySessionId(Long sessionId);

    @Query("""
        SELECT q FROM Question q
        WHERE q.session.user.id = :userId
          AND (:keyword IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%',:keyword,'%')))
          AND (:category IS NULL OR q.category = :category)
          AND (:difficulty IS NULL OR q.difficulty = :difficulty)
          AND (:technology IS NULL OR q.technology = :technology)
        ORDER BY q.createdAt DESC
        """)
    Page<Question> searchQuestions(@Param("userId")     Long userId,
                                   @Param("keyword")    String keyword,
                                   @Param("category")   String category,
                                   @Param("difficulty") String difficulty,
                                   @Param("technology") String technology,
                                   Pageable pageable);

    long countBySessionUserId(Long userId);
}
