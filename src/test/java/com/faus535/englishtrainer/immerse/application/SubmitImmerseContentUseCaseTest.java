package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentStatus;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseContentRepository;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseExerciseRepository;
import com.faus535.englishtrainer.immerse.infrastructure.StubImmerseAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubmitImmerseContentUseCaseTest {

    private InMemoryImmerseContentRepository contentRepository;
    private InMemoryImmerseExerciseRepository exerciseRepository;
    private SubmitImmerseContentUseCase useCase;

    @BeforeEach
    void setUp() {
        contentRepository = new InMemoryImmerseContentRepository();
        exerciseRepository = new InMemoryImmerseExerciseRepository();
        useCase = new SubmitImmerseContentUseCase(contentRepository, exerciseRepository, new StubImmerseAiPort());
    }

    @Test
    void processesContentAndCreatesExercises() throws Exception {
        UUID userId = UUID.randomUUID();

        ImmerseContent result = useCase.execute(userId, "https://example.com", "Test", "Some English text", "b1");

        assertEquals(ImmerseContentStatus.PROCESSED, result.status());
        assertEquals("b1", result.cefrLevel());
        assertEquals(2, result.extractedVocabulary().size());

        var exercises = exerciseRepository.findByContentId(result.id());
        assertEquals(2, exercises.size());
    }

    @Test
    void worksWithoutSourceUrl() throws Exception {
        UUID userId = UUID.randomUUID();

        ImmerseContent result = useCase.execute(userId, null, "Pasted Text", "Direct paste of text", null);

        assertEquals(ImmerseContentStatus.PROCESSED, result.status());
        assertNull(result.sourceUrl());
    }
}
