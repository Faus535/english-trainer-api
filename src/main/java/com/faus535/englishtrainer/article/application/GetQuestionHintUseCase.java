package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.error.ArticleQuestionNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;

@UseCase
public class GetQuestionHintUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleQuestionRepository questionRepository;

    GetQuestionHintUseCase(ArticleReadingRepository articleReadingRepository,
                            ArticleQuestionRepository questionRepository) {
        this.articleReadingRepository = articleReadingRepository;
        this.questionRepository = questionRepository;
    }

    public String execute(UUID userId, ArticleReadingId articleId, ArticleQuestionId questionId)
            throws ArticleNotFoundException, ArticleAccessDeniedException, ArticleQuestionNotFoundException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }

        ArticleQuestion question = questionRepository.findById(questionId)
                .filter(q -> q.articleReadingId().equals(articleId))
                .orElseThrow(() -> new ArticleQuestionNotFoundException(questionId));

        return question.hintText();
    }
}
