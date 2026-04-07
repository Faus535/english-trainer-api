package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;
import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;

@UseCase
public class MarkWordUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleMarkedWordRepository markedWordRepository;
    private final ArticleAiPort aiPort;
    private final ReviewItemRepository reviewItemRepository;

    MarkWordUseCase(ArticleReadingRepository articleReadingRepository,
                    ArticleMarkedWordRepository markedWordRepository,
                    ArticleAiPort aiPort,
                    ReviewItemRepository reviewItemRepository) {
        this.articleReadingRepository = articleReadingRepository;
        this.markedWordRepository = markedWordRepository;
        this.aiPort = aiPort;
        this.reviewItemRepository = reviewItemRepository;
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
                tr.translation(), contextSentence);
        markedWordRepository.save(marked);

        ReviewItem reviewItem = ReviewItem.create(userId, ReviewSourceType.ARTICLE,
                marked.id().value(), wordOrPhrase, tr.translation());
        reviewItemRepository.save(reviewItem);

        return marked;
    }
}
