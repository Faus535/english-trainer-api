package com.faus535.englishtrainer.article.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaArticleMarkedWordRepository extends JpaRepository<ArticleMarkedWordEntity, UUID> {

    List<ArticleMarkedWordEntity> findByArticleReadingIdAndUserId(UUID articleReadingId, UUID userId);
}
