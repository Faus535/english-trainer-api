package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class GetPreReadingUseCase {

    private final ArticleReadingRepository articleReadingRepository;
    private final ArticleAiPort aiPort;

    GetPreReadingUseCase(ArticleReadingRepository articleReadingRepository, ArticleAiPort aiPort) {
        this.articleReadingRepository = articleReadingRepository;
        this.aiPort = aiPort;
    }

    public PreReadingData execute(UUID userId, ArticleReadingId articleId)
            throws ArticleNotFoundException, ArticleAccessDeniedException, ArticleAiException {
        ArticleReading article = articleReadingRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!article.userId().equals(userId)) {
            throw new ArticleAccessDeniedException(articleId);
        }

        String articleText = article.paragraphs().stream()
                .map(ArticleParagraph::content)
                .collect(Collectors.joining("\n\n"));

        ArticleAiPort.PreReadingResult result = aiPort.generatePreReading(articleText, article.level().value());

        return new PreReadingData(
                result.keyWords().stream()
                        .map(k -> new KeyWord(k.word(), k.translation(), k.definition()))
                        .toList(),
                result.predictiveQuestion());
    }
}
