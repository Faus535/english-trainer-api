package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

import java.util.List;
import java.util.UUID;

@UseCase
public class GenerateArticleUseCase {

    private final ArticleReadingRepository repository;
    private final ArticleAiPort aiPort;
    private final UserProfileRepository userProfileRepository;

    GenerateArticleUseCase(ArticleReadingRepository repository, ArticleAiPort aiPort,
                            UserProfileRepository userProfileRepository) {
        this.repository = repository;
        this.aiPort = aiPort;
        this.userProfileRepository = userProfileRepository;
    }

    public ArticleReading execute(UUID userId, ArticleTopic topic, ArticleLevel level)
            throws UserProfileNotFoundException, ArticleAiException {
        userProfileRepository.findById(new UserProfileId(userId))
                .orElseThrow(() -> new UserProfileNotFoundException(new UserProfileId(userId)));

        ArticleReading reading = ArticleReading.create(userId, topic, level);

        ArticleAiPort.ArticleGenerateResult result = aiPort.generateArticle(topic.value(), level.value());

        List<ArticleParagraph> paragraphs = result.paragraphs().stream()
                .map(p -> ArticleParagraph.create(
                        reading.id(),
                        p.content(),
                        p.orderIndex(),
                        ArticleSpeaker.fromString(p.speaker())))
                .toList();

        ArticleReading withContent = reading.withTitleAndParagraphs(result.title(), paragraphs);
        repository.save(withContent);
        return withContent;
    }
}
