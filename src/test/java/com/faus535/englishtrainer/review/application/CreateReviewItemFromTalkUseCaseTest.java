package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import com.faus535.englishtrainer.talk.domain.TalkConversationId;
import com.faus535.englishtrainer.talk.domain.TalkCorrection;
import com.faus535.englishtrainer.talk.domain.event.TalkConversationCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateReviewItemFromTalkUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InMemoryReviewItemRepository repository;
    private CreateReviewItemFromTalkUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReviewItemRepository();
        useCase = new CreateReviewItemFromTalkUseCase(repository);
    }

    @Test
    void createsItemsFromCorrections() {
        TalkCorrection correction = new TalkCorrection(
                List.of("'I want to order' -> 'I'd like to order'"),
                List.of("latte"), List.of(), "Good try!", null);
        TalkConversationCompletedEvent event = new TalkConversationCompletedEvent(
                TalkConversationId.generate(), USER_ID, List.of(correction), 10);

        useCase.execute(event);

        assertEquals(2, repository.countByUserId(USER_ID));
    }

    @Test
    void skipsWhenNoCorrections() {
        TalkConversationCompletedEvent event = new TalkConversationCompletedEvent(
                TalkConversationId.generate(), USER_ID, List.of(), 5);

        useCase.execute(event);

        assertEquals(0, repository.countByUserId(USER_ID));
    }

    @Test
    void skipsDuplicateCorrections() {
        TalkCorrection correction = new TalkCorrection(
                List.of("Fix 1"), List.of(), List.of(), "Good!", null);
        TalkConversationCompletedEvent event1 = new TalkConversationCompletedEvent(
                TalkConversationId.generate(), USER_ID, List.of(correction), 5);
        TalkConversationCompletedEvent event2 = new TalkConversationCompletedEvent(
                TalkConversationId.generate(), USER_ID, List.of(correction), 5);

        useCase.execute(event1);
        useCase.execute(event2);

        // Should still be 1 because same correction content generates same sourceId
        assertEquals(1, repository.countByUserId(USER_ID));
    }

    @Test
    void execute_setsContextFromCorrectionAndUserMessage() {
        TalkCorrection correction = new TalkCorrection(
                List.of("'I have 20 years' -> 'I am 20 years old'"),
                List.of(), List.of(), "Good!", "I have 20 years old");
        TalkConversationCompletedEvent event = new TalkConversationCompletedEvent(
                TalkConversationId.generate(), USER_ID, List.of(correction), 4);

        useCase.execute(event);

        var item = repository.findAll(USER_ID).get(0);
        assertEquals("I have 20 years old", item.contextSentence());
        assertEquals("'I have 20 years' -> 'I am 20 years old'", item.targetWord());
    }
}
