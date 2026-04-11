package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingRepository;
import com.faus535.englishtrainer.article.domain.ArticleStatus;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;

@UseCase
public class UpdateArticleProgressUseCase {

    private final ArticleReadingRepository articleReadingRepository;

    UpdateArticleProgressUseCase(ArticleReadingRepository articleReadingRepository) {
        this.articleReadingRepository = articleReadingRepository;
    }

    public void execute(UUID userId, ArticleReadingId articleId, int paragraphIndex, int questionIndex)
            throws ArticleNotFoundException, ArticleAccessDeniedException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }
        if (article.status() == ArticleStatus.COMPLETED) {
            return;
        }
        articleReadingRepository.save(article.withProgress(paragraphIndex, questionIndex));
    }
}
