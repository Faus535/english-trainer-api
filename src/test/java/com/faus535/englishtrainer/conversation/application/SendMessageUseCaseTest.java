package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.conversation.infrastructure.InMemoryConversationRepository;
import com.faus535.englishtrainer.conversation.infrastructure.StubAiTutorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageUseCaseTest {

    private InMemoryConversationRepository repository;
    private StubAiTutorPort aiTutorPort;
    private SendMessageUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConversationRepository();
        aiTutorPort = new StubAiTutorPort();
        useCase = new SendMessageUseCase(repository, aiTutorPort);
    }

    @Test
    void shouldSendMessageAndGetResponse()
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        Conversation conversation = ConversationMother.active();
        repository.save(conversation);

        TutorFeedback feedback = new TutorFeedback(
                List.of("Use 'I like to travel' instead of 'I like travel'"),
                List.of("Consider using 'journey' or 'trip'"),
                List.of(), "Great effort!");
        aiTutorPort.willRespond("That's great! Where did you travel?", feedback);

        SendMessageUseCase.SendMessageResult result = useCase.execute(
                conversation.id().value(), "I like travel to Spain", 0.9f);

        assertEquals("That's great! Where did you travel?", result.content());
        assertNotNull(result.feedback());
        assertEquals(1, result.feedback().grammarCorrections().size());
    }

    @Test
    void shouldFailWhenConversationNotFound() {
        assertThrows(ConversationNotFoundException.class, () ->
                useCase.execute(UUID.randomUUID(), "Hello", 0.95f));
    }

    @Test
    void shouldFailWhenConversationIsCompleted() {
        Conversation conversation = ConversationMother.completed();
        repository.save(conversation);

        assertThrows(ConversationAlreadyEndedException.class, () ->
                useCase.execute(conversation.id().value(), "Hello", 0.95f));
    }
}
