package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversation;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationMother;
import com.faus535.englishtrainer.pronunciation.domain.event.PronunciationMiniConversationCompletedEvent;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationMiniConversationNotFoundException;
import com.faus535.englishtrainer.pronunciation.infrastructure.InMemoryPronunciationMiniConversationRepository;
import com.faus535.englishtrainer.pronunciation.infrastructure.StubPronunciationAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EvaluateMiniConversationTurnUseCaseTest {

    private InMemoryPronunciationMiniConversationRepository conversationRepository;
    private StubPronunciationAiPort aiPort;
    private ApplicationEventPublisher eventPublisher;
    private EvaluateMiniConversationTurnUseCase useCase;

    @BeforeEach
    void setUp() {
        conversationRepository = new InMemoryPronunciationMiniConversationRepository();
        aiPort = new StubPronunciationAiPort();
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new EvaluateMiniConversationTurnUseCase(conversationRepository, aiPort, eventPublisher);
    }

    @Test
    void shouldReturnScoreAndNextPromptWhenConversationIsActive() throws Exception {
        PronunciationMiniConversation conversation = PronunciationMiniConversationMother.active();
        conversationRepository.add(conversation);
        List<WordConfidenceDto> confidences = List.of(new WordConfidenceDto("think", 0.75));

        MiniConversationTurnResultDto result = useCase.execute(
                conversation.id().value(), "I think the weather is nice today.", confidences);

        assertNotNull(result);
        assertTrue(result.score() >= 0 && result.score() <= 100);
        assertNotNull(result.nextPrompt());
    }

    @Test
    void shouldReturnIsCompleteTrueWhenMaxTurnsReached() throws Exception {
        PronunciationMiniConversation conversation = PronunciationMiniConversationMother.nearCompletion();
        conversationRepository.add(conversation);
        List<WordConfidenceDto> confidences = List.of(new WordConfidenceDto("thought", 0.8));

        MiniConversationTurnResultDto result = useCase.execute(
                conversation.id().value(), "I thought about the theory.", confidences);

        assertTrue(result.isComplete());
    }

    @Test
    void shouldFireCompletedEventWhenConversationCompletes() throws Exception {
        PronunciationMiniConversation conversation = PronunciationMiniConversationMother.nearCompletion();
        conversationRepository.add(conversation);
        List<WordConfidenceDto> confidences = List.of(new WordConfidenceDto("thought", 0.8));

        useCase.execute(conversation.id().value(), "I thought about the theory.", confidences);

        verify(eventPublisher, times(1))
                .publishEvent(any(PronunciationMiniConversationCompletedEvent.class));
    }

    @Test
    void shouldNotFireEventOnIntermediateTurn() throws Exception {
        PronunciationMiniConversation conversation = PronunciationMiniConversationMother.active();
        conversationRepository.add(conversation);
        List<WordConfidenceDto> confidences = List.of(new WordConfidenceDto("think", 0.75));

        useCase.execute(conversation.id().value(), "I think the weather is nice.", confidences);

        verify(eventPublisher, never())
                .publishEvent(any(PronunciationMiniConversationCompletedEvent.class));
    }

    @Test
    void shouldThrowNotFoundWhenConversationDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        List<WordConfidenceDto> confidences = List.of(new WordConfidenceDto("test", 0.8));

        assertThrows(PronunciationMiniConversationNotFoundException.class,
                () -> useCase.execute(nonExistentId, "test", confidences));
    }

    @Test
    void shouldPropagateAiExceptionWhenAiFails() throws Exception {
        PronunciationMiniConversation conversation = PronunciationMiniConversationMother.active();
        conversationRepository.add(conversation);
        aiPort.setShouldThrowAiException(true);
        List<WordConfidenceDto> confidences = List.of(new WordConfidenceDto("test", 0.8));

        assertThrows(com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException.class,
                () -> useCase.execute(conversation.id().value(), "test", confidences));
    }
}
