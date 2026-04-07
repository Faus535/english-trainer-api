package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.infrastructure.FailingStubArticleAiPort;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.article.infrastructure.StubArticleAiPort;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GenerateArticleUseCaseTest {

    private InMemoryArticleReadingRepository repository;
    private InMemoryUserProfileRepository userProfileRepository;
    private GenerateArticleUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArticleReadingRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        useCase = new GenerateArticleUseCase(repository, new StubArticleAiPort(), userProfileRepository);
    }

    private UUID createUser() {
        UserProfileId profileId = UserProfileId.generate();
        userProfileRepository.save(UserProfile.reconstitute(profileId, null, 0,
                java.time.Instant.now(), java.time.Instant.now()));
        return profileId.value();
    }

    @Test
    void happyPathSavesArticleWithTitleAndParagraphs() throws Exception {
        UUID userId = createUser();

        ArticleReading result = useCase.execute(userId, new ArticleTopic("Climate change"), ArticleLevel.B2);

        assertNotNull(result.id());
        assertEquals("EU's New Climate Targets Spark Debate", result.title());
        assertEquals(ArticleStatus.IN_PROGRESS, result.status());
        assertEquals(3, result.paragraphs().size());
        assertEquals(ArticleSpeaker.AI, result.paragraphs().get(0).speaker());
        assertEquals(1, repository.count());
    }

    @Test
    void aiFailureThrowsArticleAiException() {
        UUID userId = createUser();
        useCase = new GenerateArticleUseCase(repository, new FailingStubArticleAiPort(), userProfileRepository);

        assertThrows(ArticleAiException.class,
                () -> useCase.execute(userId, new ArticleTopic("Climate"), ArticleLevel.B1));
        assertEquals(0, repository.count());
    }

    @Test
    void userNotFoundThrowsUserProfileNotFoundException() {
        UUID unknownUserId = UUID.randomUUID();

        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(unknownUserId, new ArticleTopic("Tech"), ArticleLevel.B1));
        assertEquals(0, repository.count());
    }
}
