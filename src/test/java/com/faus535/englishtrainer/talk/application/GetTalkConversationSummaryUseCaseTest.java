package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetTalkConversationSummaryUseCaseTest {

    private InMemoryTalkConversationRepository repository;
    private GetTalkConversationSummaryUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTalkConversationRepository();
        useCase = new GetTalkConversationSummaryUseCase(repository);
    }

    @Test
    void returnsSummaryForCompletedConversation() throws Exception {
        TalkConversation conversation = TalkConversationMother.completed();
        repository.save(conversation);

        var result = useCase.execute(conversation.id().value());

        assertEquals("Good session.", result.summary());
        assertNotNull(result.evaluation());
        assertEquals(78, result.evaluation().overallScore());
    }

    @Test
    void returnsTurnCountEqualToMessageCount() throws Exception {
        TalkConversation conversation = TalkConversationMother.withMessages(4);
        repository.save(conversation);

        var result = useCase.execute(conversation.id().value());

        assertEquals(4, result.turnCount());
    }

    @Test
    void throwsWhenConversationNotFound() {
        UUID unknownId = UUID.randomUUID();

        assertThrows(TalkConversationNotFoundException.class,
                () -> useCase.execute(unknownId));
    }

    @Test
    void returnsNullEvaluationForActiveConversation() throws Exception {
        TalkConversation conversation = TalkConversationMother.active();
        repository.save(conversation);

        var result = useCase.execute(conversation.id().value());

        assertNull(result.evaluation());
        assertEquals(0, result.turnCount());
    }
}
