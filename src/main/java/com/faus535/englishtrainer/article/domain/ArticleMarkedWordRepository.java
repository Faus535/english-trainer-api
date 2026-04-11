package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleMarkedWordRepository {

    void save(ArticleMarkedWord word) throws DuplicateMarkedWordException;

    List<ArticleMarkedWord> findByArticleIdAndUserId(ArticleReadingId articleReadingId, UUID userId);

    Optional<ArticleMarkedWord> findById(ArticleMarkedWordId id);

    void update(ArticleMarkedWord word);
}
