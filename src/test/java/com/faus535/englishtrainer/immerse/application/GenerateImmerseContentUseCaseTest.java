package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentStatus;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseExerciseRepository;
import com.faus535.englishtrainer.immerse.infrastructure.StubImmerseAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GenerateImmerseContentUseCaseTest {

    private InMemoryImmerseContentRepository contentRepository;
    private InMemoryImmerseExerciseRepository exerciseRepository;
    private GenerateImmerseContentUseCase useCase;

    @BeforeEach
    void setUp() {
        contentRepository = new InMemoryImmerseContentRepository();
        exerciseRepository = new InMemoryImmerseExerciseRepository();
        useCase = new GenerateImmerseContentUseCase(contentRepository, exerciseRepository, new StubImmerseAiPort());
    }

    @Test
    void generatesTextContentSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();

        ImmerseContent result = useCase.execute(userId, ContentType.TEXT, "b1", "city life");

        assertEquals(ImmerseContentStatus.PROCESSED, result.status());
        assertEquals(ContentType.TEXT, result.contentType());
        assertEquals("A Day in the City", result.title());
        assertEquals("b1", result.cefrLevel());
        assertEquals(2, result.extractedVocabulary().size());
        assertNull(result.sourceUrl());
    }

    @Test
    void generatesAudioContentSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();

        ImmerseContent result = useCase.execute(userId, ContentType.AUDIO, "b2", null);

        assertEquals(ImmerseContentStatus.PROCESSED, result.status());
        assertEquals(ContentType.AUDIO, result.contentType());
    }

    @Test
    void worksWithoutTopicOrLevel() throws Exception {
        UUID userId = UUID.randomUUID();

        ImmerseContent result = useCase.execute(userId, ContentType.VIDEO, null, null);

        assertEquals(ImmerseContentStatus.PROCESSED, result.status());
        assertEquals(ContentType.VIDEO, result.contentType());
        assertNotNull(result.rawText());
    }

    @Test
    void savesExercisesFromAiResult() throws Exception {
        UUID userId = UUID.randomUUID();

        ImmerseContent result = useCase.execute(userId, ContentType.TEXT, "b1", null);

        var exercises = exerciseRepository.findByContentId(result.id());
        assertEquals(3, exercises.size());
    }
}
