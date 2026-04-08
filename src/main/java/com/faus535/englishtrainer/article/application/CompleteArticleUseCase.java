package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAlreadyCompletedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class CompleteArticleUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleMarkedWordRepository markedWordRepository;
    private final ArticleQuestionAnswerRepository answerRepository;
    private final ArticleQuestionRepository questionRepository;
    private final ApplicationEventPublisher eventPublisher;

    CompleteArticleUseCase(ArticleReadingRepository articleReadingRepository,
                            ArticleMarkedWordRepository markedWordRepository,
                            ArticleQuestionAnswerRepository answerRepository,
                            ArticleQuestionRepository questionRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.articleReadingRepository = articleReadingRepository;
        this.markedWordRepository = markedWordRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ArticleReading execute(UUID userId, ArticleReadingId articleId)
            throws ArticleNotFoundException, ArticleAccessDeniedException,
            ArticleAlreadyCompletedException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }

        int markedWordsCount = markedWordRepository
                .findByArticleIdAndUserId(articleId, userId).size();

        int correctAnswersCount = 0;
        for (ArticleQuestion q : questionRepository.findByArticleReadingId(articleId)) {
            var answer = answerRepository.findByQuestionId(q.id());
            if (answer.isPresent() && answer.get().isContentCorrect()) {
                correctAnswersCount++;
            }
        }

        int xpEarned = 25 + (5 * correctAnswersCount) + (2 * markedWordsCount);

        ArticleReading completed = article.complete(xpEarned);
        articleReadingRepository.save(completed);

        completed.pullDomainEvents().forEach(event -> {
            if (event instanceof ArticleReadingCompletedEvent e) {
                eventPublisher.publishEvent(e);
            }
        });

        return completed;
    }
}
