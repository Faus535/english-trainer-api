package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseExerciseId;
import com.faus535.englishtrainer.immerse.domain.event.ImmerseExerciseAnsweredEvent;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateReviewItemFromImmerseUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InMemoryReviewItemRepository repository;
    private CreateReviewItemFromImmerseUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReviewItemRepository();
        useCase = new CreateReviewItemFromImmerseUseCase(repository);
    }

    @Test
    void createsItemFromIncorrectAnswer() {
        ImmerseExerciseAnsweredEvent event = new ImmerseExerciseAnsweredEvent(
                USER_ID, ImmerseExerciseId.generate(),
                "What does 'drought' mean?", "A long period without rain", "A type of storm");

        useCase.execute(event);

        assertEquals(1, repository.countByUserId(USER_ID));
    }

    @Test
    void skipsDuplicateExercise() {
        ImmerseExerciseId exerciseId = ImmerseExerciseId.generate();
        ImmerseExerciseAnsweredEvent event = new ImmerseExerciseAnsweredEvent(
                USER_ID, exerciseId,
                "What does 'drought' mean?", "A long period without rain", "A type of storm");

        useCase.execute(event);
        useCase.execute(event);

        assertEquals(1, repository.countByUserId(USER_ID));
    }

    @Test
    void execute_setsContextSentenceFromQuestion() {
        ImmerseExerciseId exerciseId = ImmerseExerciseId.generate();
        ImmerseExerciseAnsweredEvent event = new ImmerseExerciseAnsweredEvent(
                USER_ID, exerciseId,
                "What does 'drought' mean?", "A long period without rain", "A type of storm");

        useCase.execute(event);

        var item = repository.findAll(USER_ID).get(0);
        assertEquals("What does 'drought' mean?", item.contextSentence());
        assertEquals("A long period without rain", item.targetWord());
    }
}
