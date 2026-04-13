package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.infrastructure.StubPronunciationAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzePronunciationUseCaseTest {

    private StubPronunciationAiPort aiPort;
    private AnalyzePronunciationUseCase useCase;

    @BeforeEach
    void setUp() {
        aiPort = new StubPronunciationAiPort();
        useCase = new AnalyzePronunciationUseCase(aiPort);
    }

    @Test
    void shouldReturnAnalysisWhenTextIsValid() throws Exception {
        PronunciationAnalysisDto result = useCase.execute("thought", "b1");

        assertNotNull(result);
        assertNotNull(result.ipa());
        assertFalse(result.ipa().isBlank());
        assertFalse(result.tips().isEmpty());
        assertFalse(result.minimalPairs().isEmpty());
    }

    @Test
    void shouldPropagateAiExceptionWhenAiFails() {
        aiPort.setShouldThrowAiException(true);

        assertThrows(PronunciationAiException.class,
                () -> useCase.execute("thought", "b1"));
    }

    @Test
    void shouldPreserveInputTextInResult() throws Exception {
        String inputText = "through";

        PronunciationAnalysisDto result = useCase.execute(inputText, "b2");

        assertEquals(inputText, result.text());
    }
}
