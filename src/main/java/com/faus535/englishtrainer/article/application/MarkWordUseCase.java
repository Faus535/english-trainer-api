package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;
import com.faus535.englishtrainer.article.domain.event.ArticleWordMarkedEvent;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

@UseCase
public class MarkWordUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleMarkedWordRepository markedWordRepository;
    private final ArticleAiPort aiPort;
    private final ApplicationEventPublisher eventPublisher;

    MarkWordUseCase(ArticleReadingRepository articleReadingRepository,
                    ArticleMarkedWordRepository markedWordRepository,
                    ArticleAiPort aiPort,
                    ApplicationEventPublisher eventPublisher) {
        this.articleReadingRepository = articleReadingRepository;
        this.markedWordRepository = markedWordRepository;
        this.aiPort = aiPort;
        this.eventPublisher = eventPublisher;
    }

    public ArticleMarkedWord execute(UUID userId, ArticleReadingId articleId,
                                      String wordOrPhrase, String contextSentence)
            throws ArticleNotFoundException, ArticleAccessDeniedException,
            ArticleAiException, DuplicateMarkedWordException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }

        ArticleAiPort.ArticleTranslationResult tr = aiPort.translateWord(wordOrPhrase, contextSentence);

        ArticleMarkedWord marked = ArticleMarkedWord.create(articleId, userId, wordOrPhrase,
                tr.translation(), tr.englishDefinition(), contextSentence);
        markedWordRepository.save(marked);

        eventPublisher.publishEvent(new ArticleWordMarkedEvent(
                articleId.value(), userId, marked.id().value(),
                wordOrPhrase, tr.translation(), contextSentence));

        return marked;
    }
}
