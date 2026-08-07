package com.nexoraa.interviewgen.repository;

import com.nexoraa.interviewgen.entity.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);

    Optional<UserFavorite> findByUserIdAndQuestionId(Long userId, Long questionId);

    @Query("""
        SELECT uf FROM UserFavorite uf
        JOIN FETCH uf.question q
        WHERE uf.user.id = :userId
        ORDER BY uf.createdAt DESC
        """)
    Page<UserFavorite> findByUserIdWithQuestion(@Param("userId") Long userId, Pageable pageable);

    long countByUserId(Long userId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM UserFavorite uf WHERE uf.question.session.id = :sessionId")
    void deleteBySessionId(@Param("sessionId") Long sessionId);
}
