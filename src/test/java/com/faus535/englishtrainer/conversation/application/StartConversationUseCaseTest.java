package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.ConversationStatus;
import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.ConversationMother;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.MaxConversationsExceededException;
import com.faus535.englishtrainer.conversation.infrastructure.InMemoryConversationRepository;
import com.faus535.englishtrainer.conversation.infrastructure.StubAiTutorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartConversationUseCaseTest {

    private InMemoryConversationRepository repository;
    private StubAiTutorPort aiTutorPort;
    private ApplicationEventPublisher eventPublisher;
    private StartConversationUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConversationRepository();
        aiTutorPort = new StubAiTutorPort();
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new StartConversationUseCase(repository, aiTutorPort, eventPublisher);
    }

    @Test
    void shouldStartConversationWithOpeningMessage() throws MaxConversationsExceededException, AiTutorException {
        UUID userId = UUID.randomUUID();

        Conversation result = useCase.execute(userId, "b1", "Travel");

        assertNotNull(result.id());
        assertEquals(ConversationStatus.ACTIVE, result.status());
        assertEquals("b1", result.level().value());
        assertEquals("Travel", result.topic());
        assertEquals(1, result.turns().size());
        assertEquals("assistant", result.turns().getFirst().role());
    }

    @Test
    void shouldRejectWhenMaxConversationsReached() {
        UUID userId = UUID.randomUUID();
        repository.save(ConversationMother.withUserId(userId));
        repository.save(ConversationMother.withUserId(userId));
        repository.save(ConversationMother.withUserId(userId));

        assertThrows(MaxConversationsExceededException.class, () ->
                useCase.execute(userId, "a1", null));
    }

    @Test
    void shouldFailWhenAiIsUnavailable() {
        aiTutorPort.willFail();

        assertThrows(AiTutorException.class, () ->
                useCase.execute(UUID.randomUUID(), "b1", "Food"));
    }
}
