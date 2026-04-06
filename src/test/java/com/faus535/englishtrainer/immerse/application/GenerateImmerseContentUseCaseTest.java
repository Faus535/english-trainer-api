package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentStatus;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseExerciseRepository;
import com.faus535.englishtrainer.immerse.infrastructure.StubImmerseAiPort;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GenerateImmerseContentUseCaseTest {

    private InMemoryImmerseContentRepository contentRepository;
    private InMemoryImmerseExerciseRepository exerciseRepository;
    private InMemoryUserProfileRepository userProfileRepository;
    private GenerateImmerseContentUseCase useCase;

    @BeforeEach
    void setUp() {
        contentRepository = new InMemoryImmerseContentRepository();
        exerciseRepository = new InMemoryImmerseExerciseRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        useCase = new GenerateImmerseContentUseCase(contentRepository, exerciseRepository, new StubImmerseAiPort(), userProfileRepository);
    }

    private UUID createProfileAndReturnId() {
        UserProfileId profileId = UserProfileId.generate();
        Instant now = Instant.now();
        userProfileRepository.save(UserProfile.reconstitute(profileId, null, 0, now, now));
        return profileId.value();
    }

    @Test
    void returnsPendingContentImmediately() throws Exception {
        UUID userId = createProfileAndReturnId();

        ImmerseContent result = useCase.execute(userId, ContentType.TEXT, "b1", "city life");

        assertEquals(ImmerseContentStatus.PENDING, result.status());
        assertEquals(ContentType.TEXT, result.contentType());
        assertEquals("TEXT content", result.title());
        assertNull(result.processedText());
        assertTrue(result.extractedVocabulary().isEmpty());
        assertNull(result.sourceUrl());
    }

    @Test
    void savesContentInRepository() throws Exception {
        UUID userId = createProfileAndReturnId();

        ImmerseContent result = useCase.execute(userId, ContentType.AUDIO, "b2", null);

        assertEquals(ImmerseContentStatus.PENDING, result.status());
        assertEquals(ContentType.AUDIO, result.contentType());
        assertFalse(contentRepository.findByUserId(userId, 0, 100).isEmpty());
    }

    @Test
    void doesNotSaveExercisesSynchronously() throws Exception {
        UUID userId = createProfileAndReturnId();

        ImmerseContent result = useCase.execute(userId, ContentType.TEXT, "b1", null);

        var exercises = exerciseRepository.findByContentId(result.id());
        assertTrue(exercises.isEmpty());
    }

    @Test
    void throwsUserProfileNotFoundWhenProfileMissing() {
        UUID missingId = UUID.randomUUID();
        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(missingId, ContentType.TEXT, "b1", "topic"));
        assertTrue(contentRepository.findByUserId(missingId, 0, 100).isEmpty());
    }
}
