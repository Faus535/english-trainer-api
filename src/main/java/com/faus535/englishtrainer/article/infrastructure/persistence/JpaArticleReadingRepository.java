package com.faus535.englishtrainer.article.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaArticleReadingRepository extends JpaRepository<ArticleReadingEntity, UUID> {

    List<ArticleReadingEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
