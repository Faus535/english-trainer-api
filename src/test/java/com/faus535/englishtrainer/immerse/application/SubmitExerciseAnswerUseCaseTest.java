package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseExerciseNotFoundException;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseExerciseRepository;
import com.faus535.englishtrainer.immerse.infrastructure.InMemoryImmerseSubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SubmitExerciseAnswerUseCaseTest {

    private InMemoryImmerseExerciseRepository exerciseRepository;
    private InMemoryImmerseSubmissionRepository submissionRepository;
    private SubmitExerciseAnswerUseCase useCase;

    @BeforeEach
    void setUp() {
        exerciseRepository = new InMemoryImmerseExerciseRepository();
        submissionRepository = new InMemoryImmerseSubmissionRepository();
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        useCase = new SubmitExerciseAnswerUseCase(exerciseRepository, submissionRepository, publisher);
    }

    @Test
    void correctAnswerReturnsTrue() throws Exception {
        ImmerseContentId contentId = ImmerseContentId.generate();
        ImmerseExercise exercise = ImmerseExerciseMother.multipleChoice(contentId);
        exerciseRepository.saveAll(List.of(exercise));

        var result = useCase.execute(UUID.randomUUID(), exercise.id().value(), "A long period without rain");

        assertTrue(result.correct());
        assertEquals(1, submissionRepository.findAll().size());
    }

    @Test
    void incorrectAnswerReturnsFalse() throws Exception {
        ImmerseContentId contentId = ImmerseContentId.generate();
        ImmerseExercise exercise = ImmerseExerciseMother.multipleChoice(contentId);
        exerciseRepository.saveAll(List.of(exercise));

        var result = useCase.execute(UUID.randomUUID(), exercise.id().value(), "A type of storm");

        assertFalse(result.correct());
        assertEquals("A long period without rain", result.correctAnswer());
    }

    @Test
    void throwsWhenExerciseNotFound() {
        assertThrows(ImmerseExerciseNotFoundException.class,
                () -> useCase.execute(UUID.randomUUID(), UUID.randomUUID(), "answer"));
    }
}
