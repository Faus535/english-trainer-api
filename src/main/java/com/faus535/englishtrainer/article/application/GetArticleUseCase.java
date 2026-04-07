package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingRepository;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;

@UseCase
public class GetArticleUseCase {

    private final ArticleReadingRepository repository;

    GetArticleUseCase(ArticleReadingRepository repository) {
        this.repository = repository;
    }

    public ArticleReading execute(UUID userId, ArticleReadingId id)
            throws ArticleNotFoundException, ArticleAccessDeniedException {
        ArticleReading reading = repository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        if (!reading.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(id);
        }
        return reading;
    }
}
