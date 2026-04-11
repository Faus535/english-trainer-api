package com.faus535.englishtrainer.article.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleReadingRepository {

    void save(ArticleReading reading);

    Optional<ArticleReading> findById(ArticleReadingId id);

    List<ArticleReading> findByUserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByUserIdAndCreatedAtAfter(UUID userId, Instant since);

    void deleteById(ArticleReadingId id);
}
