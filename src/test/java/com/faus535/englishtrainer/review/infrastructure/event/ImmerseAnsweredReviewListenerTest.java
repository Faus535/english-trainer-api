package com.faus535.englishtrainer.review.infrastructure.event;

import com.faus535.englishtrainer.immerse.domain.ImmerseExerciseId;
import com.faus535.englishtrainer.immerse.domain.event.ImmerseExerciseAnsweredEvent;
import com.faus535.englishtrainer.review.application.CreateReviewItemFromImmerseUseCase;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImmerseAnsweredReviewListenerTest {

    @Test
    void eventCreatesReviewItem() {
        InMemoryReviewItemRepository repository = new InMemoryReviewItemRepository();
        CreateReviewItemFromImmerseUseCase useCase = new CreateReviewItemFromImmerseUseCase(repository);
        ImmerseAnsweredReviewListener listener = new ImmerseAnsweredReviewListener(useCase);

        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ImmerseExerciseAnsweredEvent event = new ImmerseExerciseAnsweredEvent(
                userId, ImmerseExerciseId.generate(),
                "What does 'drought' mean?", "A long period without rain", "A storm");

        listener.handle(event);

        assertEquals(1, repository.countByUserId(userId));
    }
}
