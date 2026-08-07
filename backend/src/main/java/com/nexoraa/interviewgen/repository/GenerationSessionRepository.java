package com.nexoraa.interviewgen.repository;

import com.nexoraa.interviewgen.entity.GenerationSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenerationSessionRepository extends JpaRepository<GenerationSession, Long> {

    Page<GenerationSession> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT s FROM GenerationSession s WHERE s.user.id = :userId AND s.id = :sessionId")
    java.util.Optional<GenerationSession> findByIdAndUserId(@Param("sessionId") Long sessionId,
                                                             @Param("userId")    Long userId);

    long countByUserId(Long userId);
}
