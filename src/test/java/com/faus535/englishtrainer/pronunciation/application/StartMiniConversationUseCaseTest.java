package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.infrastructure.InMemoryPronunciationMiniConversationRepository;
import com.faus535.englishtrainer.pronunciation.infrastructure.StubPronunciationAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StartMiniConversationUseCaseTest {

    private InMemoryPronunciationMiniConversationRepository conversationRepository;
    private StubPronunciationAiPort aiPort;
    private StartMiniConversationUseCase useCase;

    @BeforeEach
    void setUp() {
        conversationRepository = new InMemoryPronunciationMiniConversationRepository();
        aiPort = new StubPronunciationAiPort();
        useCase = new StartMiniConversationUseCase(conversationRepository, aiPort);
    }

    @Test
    void shouldPersistConversationWithInitialPromptWhenFocusAndLevelAreValid() throws Exception {
        UUID userId = UUID.randomUUID();

        useCase.execute(userId, "th-sound", "b1");

        assertEquals(1, conversationRepository.count());
    }

    @Test
    void shouldReturnDtoWithIdAndPromptAndTargetPhrase() throws Exception {
        UUID userId = UUID.randomUUID();

        PronunciationMiniConversationDto result = useCase.execute(userId, "th-sound", "b1");

        assertNotNull(result.id());
        assertNotNull(result.prompt());
        assertFalse(result.prompt().isBlank());
        assertNotNull(result.targetPhrase());
        assertFalse(result.targetPhrase().isBlank());
    }

    @Test
    void shouldPropagateAiExceptionWhenAiFails() {
        aiPort.setShouldThrowAiException(true);
        UUID userId = UUID.randomUUID();

        assertThrows(PronunciationAiException.class,
                () -> useCase.execute(userId, "th-sound", "b1"));
    }
}
