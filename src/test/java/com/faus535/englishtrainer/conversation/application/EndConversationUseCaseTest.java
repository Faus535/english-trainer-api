package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.ConversationMother;
import com.faus535.englishtrainer.conversation.domain.ConversationStatus;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.conversation.infrastructure.InMemoryConversationRepository;
import com.faus535.englishtrainer.conversation.infrastructure.StubAiTutorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EndConversationUseCaseTest {

    private InMemoryConversationRepository repository;
    private StubAiTutorPort aiTutorPort;
    private ApplicationEventPublisher eventPublisher;
    private EndConversationUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConversationRepository();
        aiTutorPort = new StubAiTutorPort();
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new EndConversationUseCase(repository, aiTutorPort, eventPublisher);
    }

    @Test
    void shouldEndConversationWithSummary()
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        Conversation conversation = ConversationMother.activeWithTurns();
        repository.save(conversation);
        aiTutorPort.willSummarize("Great session! You practiced travel vocabulary.");

        EndConversationUseCase.EndConversationResult result = useCase.execute(conversation.id().value());

        assertEquals(ConversationStatus.COMPLETED, result.conversation().status());
        assertEquals("Great session! You practiced travel vocabulary.", result.conversation().summary());
        assertNotNull(result.conversation().endedAt());
        assertNotNull(result.evaluation());
    }

    @Test
    void shouldFailWhenConversationAlreadyEnded() {
        Conversation conversation = ConversationMother.completed();
        repository.save(conversation);

        assertThrows(ConversationAlreadyEndedException.class, () ->
                useCase.execute(conversation.id().value()));
    }
}
