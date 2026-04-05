package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import com.faus535.englishtrainer.talk.infrastructure.StubTalkAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EndTalkConversationUseCaseTest {

    private InMemoryTalkConversationRepository repository;
    private ApplicationEventPublisher eventPublisher;
    private EndTalkConversationUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTalkConversationRepository();
        StubTalkAiPort aiPort = new StubTalkAiPort();
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new EndTalkConversationUseCase(repository, aiPort, eventPublisher);
    }

    @Test
    void endConversationReturnsSummaryAndEvaluation() throws Exception {
        TalkConversation conversation = TalkConversationMother.withMessages(4);
        repository.save(conversation);

        var result = useCase.execute(conversation.id().value());

        assertNotNull(result.summary());
        assertNotNull(result.evaluation());
        assertEquals(4, result.turnCount());

        TalkConversation updated = repository.findById(conversation.id()).orElseThrow();
        assertEquals(TalkStatus.COMPLETED, updated.status());
    }

    @Test
    void completedConversationHasEvaluation() throws Exception {
        TalkConversation conversation = TalkConversationMother.withMessages(4);
        repository.save(conversation);

        var result = useCase.execute(conversation.id().value());

        assertNotNull(result.evaluation());
        assertEquals(78, result.evaluation().overallScore());
    }

    @Test
    void throwsOnAlreadyCompleted() {
        TalkConversation conversation = TalkConversationMother.completed();
        repository.save(conversation);

        assertThrows(TalkConversationAlreadyEndedException.class,
                () -> useCase.execute(conversation.id().value()));
    }
}
