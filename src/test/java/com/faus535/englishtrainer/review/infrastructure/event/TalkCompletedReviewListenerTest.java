package com.faus535.englishtrainer.review.infrastructure.event;

import com.faus535.englishtrainer.review.application.CreateReviewItemFromTalkUseCase;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import com.faus535.englishtrainer.talk.domain.TalkConversationId;
import com.faus535.englishtrainer.talk.domain.TalkCorrection;
import com.faus535.englishtrainer.talk.domain.event.TalkConversationCompletedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TalkCompletedReviewListenerTest {

    @Test
    void eventCreatesReviewItems() {
        InMemoryReviewItemRepository repository = new InMemoryReviewItemRepository();
        CreateReviewItemFromTalkUseCase useCase = new CreateReviewItemFromTalkUseCase(repository);
        TalkCompletedReviewListener listener = new TalkCompletedReviewListener(useCase);

        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        TalkCorrection correction = new TalkCorrection(
                List.of("Grammar fix"), List.of(), List.of(), "Good!", null);
        TalkConversationCompletedEvent event = new TalkConversationCompletedEvent(
                TalkConversationId.generate(), userId, List.of(correction), 8);

        listener.handle(event);

        assertEquals(1, repository.countByUserId(userId));
    }
}
