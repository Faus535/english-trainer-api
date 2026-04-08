package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingRepository;
import com.faus535.englishtrainer.article.domain.ArticleStatus;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleCannotBeDeletedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class DeleteArticleUseCase {

    private final ArticleReadingRepository repository;

    DeleteArticleUseCase(ArticleReadingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(UUID userId, ArticleReadingId articleReadingId)
            throws ArticleNotFoundException, ArticleAccessDeniedException, ArticleCannotBeDeletedException {
        ArticleReading article = repository.findById(articleReadingId)
                .orElseThrow(() -> new ArticleNotFoundException(articleReadingId));

        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleReadingId);
        }

        if (article.status() == ArticleStatus.COMPLETED) {
            throw new ArticleCannotBeDeletedException(articleReadingId);
        }

        repository.deleteById(articleReadingId);
    }
}
