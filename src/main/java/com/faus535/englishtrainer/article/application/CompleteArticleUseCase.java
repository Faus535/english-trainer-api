package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.*;
import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class CompleteArticleUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final GenerateQuestionsUseCase generateQuestionsUseCase;
    private final ApplicationEventPublisher eventPublisher;

    CompleteArticleUseCase(ArticleReadingRepository articleReadingRepository,
                            GenerateQuestionsUseCase generateQuestionsUseCase,
                            ApplicationEventPublisher eventPublisher) {
        this.articleReadingRepository = articleReadingRepository;
        this.generateQuestionsUseCase = generateQuestionsUseCase;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ArticleReading execute(UUID userId, ArticleReadingId articleId)
            throws ArticleNotFoundException, ArticleAccessDeniedException,
            ArticleAlreadyCompletedException, ArticleAiException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }

        ArticleReading completed = article.complete();
        articleReadingRepository.save(completed);

        String fullText = completed.paragraphs().stream()
                .map(ArticleParagraph::content)
                .collect(Collectors.joining("\n\n"));

        generateQuestionsUseCase.execute(articleId, fullText, completed.level().value());

        completed.pullDomainEvents().forEach(event -> {
            if (event instanceof ArticleReadingCompletedEvent e) {
                eventPublisher.publishEvent(e);
            }
        });

        return completed;
    }
}
