package com.faus535.englishtrainer.article.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleReadingRepository {

    void save(ArticleReading reading);

    Optional<ArticleReading> findById(ArticleReadingId id);

    List<ArticleReading> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
