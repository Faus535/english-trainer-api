package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

interface JpaImmerseSubmissionRepository extends JpaRepository<ImmerseSubmissionEntity, UUID> {

    @Query("SELECT COUNT(s) FROM ImmerseSubmissionEntity s WHERE s.userId = :userId AND s.submittedAt > :since")
    long countByUserIdAndSubmittedAtAfter(UUID userId, Instant since);
}
