package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GenerateArticleUseCase {

    private final ArticleReadingRepository repository;
    private final ProcessArticleContentAsyncService asyncService;
    private final UserProfileRepository userProfileRepository;

    GenerateArticleUseCase(ArticleReadingRepository repository,
                            ProcessArticleContentAsyncService asyncService,
                            UserProfileRepository userProfileRepository) {
        this.repository = repository;
        this.asyncService = asyncService;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public ArticleReading execute(UUID userId, ArticleTopic topic, ArticleLevel level)
            throws UserProfileNotFoundException {
        userProfileRepository.findById(new UserProfileId(userId))
                .orElseThrow(() -> new UserProfileNotFoundException(new UserProfileId(userId)));

        ArticleReading reading = ArticleReading.create(userId, topic, level);
        repository.save(reading);

        asyncService.process(reading.id().value(), topic.value(), level.value());

        return reading;
    }
}
