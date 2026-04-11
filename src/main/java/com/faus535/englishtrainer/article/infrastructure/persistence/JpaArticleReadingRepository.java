package com.faus535.englishtrainer.article.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface JpaArticleReadingRepository extends JpaRepository<ArticleReadingEntity, UUID> {

    List<ArticleReadingEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT COUNT(r) > 0 FROM ArticleReadingEntity r WHERE r.userId = :userId AND r.createdAt > :since")
    boolean existsByUserIdAndCreatedAtAfter(UUID userId, Instant since);
}
