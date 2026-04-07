package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetArticleMarkedWordsUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleMarkedWordRepository markedWordRepository;

    GetArticleMarkedWordsUseCase(ArticleReadingRepository articleReadingRepository,
                                  ArticleMarkedWordRepository markedWordRepository) {
        this.articleReadingRepository = articleReadingRepository;
        this.markedWordRepository = markedWordRepository;
    }

    public List<ArticleMarkedWord> execute(UUID userId, ArticleReadingId articleId)
            throws ArticleNotFoundException, ArticleAccessDeniedException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }
        return markedWordRepository.findByArticleIdAndUserId(articleId, userId);
    }
}
