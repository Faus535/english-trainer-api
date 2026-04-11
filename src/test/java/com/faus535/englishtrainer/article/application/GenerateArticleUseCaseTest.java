package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.article.infrastructure.NoOpProcessArticleContentAsyncService;
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
    private NoOpProcessArticleContentAsyncService asyncService;
    private GenerateArticleUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArticleReadingRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        asyncService = new NoOpProcessArticleContentAsyncService();
        useCase = new GenerateArticleUseCase(repository, asyncService, userProfileRepository);
    }

    private UUID createUser() {
        UserProfileId profileId = UserProfileId.generate();
        userProfileRepository.save(UserProfile.reconstitute(profileId, null, 0, null,
                java.time.Instant.now(), java.time.Instant.now()));
        return profileId.value();
    }

    @Test
    void shouldSavePendingArticleAndTriggerAsyncProcessing() throws Exception {
        UUID userId = createUser();

        ArticleReading result = useCase.execute(userId, new ArticleTopic("Climate change"), ArticleLevel.B2);

        assertNotNull(result.id());
        assertEquals(ArticleStatus.PENDING, result.status());
        assertEquals("", result.title());
        assertTrue(result.paragraphs().isEmpty());
        assertEquals(1, repository.count());
        assertTrue(asyncService.wasProcessCalled());
    }

    @Test
    void shouldThrowUserProfileNotFoundWhenUserDoesNotExist() {
        UUID unknownUserId = UUID.randomUUID();

        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(unknownUserId, new ArticleTopic("Tech"), ArticleLevel.B1));
        assertEquals(0, repository.count());
        assertFalse(asyncService.wasProcessCalled());
    }
}
