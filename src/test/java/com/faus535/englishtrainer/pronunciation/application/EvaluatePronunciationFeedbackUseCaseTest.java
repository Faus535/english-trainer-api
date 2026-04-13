package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.infrastructure.StubPronunciationAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatePronunciationFeedbackUseCaseTest {

    private StubPronunciationAiPort aiPort;
    private EvaluatePronunciationFeedbackUseCase useCase;

    @BeforeEach
    void setUp() {
        aiPort = new StubPronunciationAiPort();
        useCase = new EvaluatePronunciationFeedbackUseCase(aiPort);
    }

    @Test
    void shouldReturnScoreAndWordFeedbackWhenRecognizedTextDiffersFromTarget() throws Exception {
        List<WordConfidenceDto> wordConfidences = List.of(
                new WordConfidenceDto("thought", 0.65),
                new WordConfidenceDto("about", 0.90)
        );

        PronunciationFeedbackDto result = useCase.execute(
                "I thought about it", "I tought about it", wordConfidences);

        assertNotNull(result);
        assertTrue(result.score() >= 0 && result.score() <= 100);
        assertFalse(result.wordFeedback().isEmpty());
        assertNotNull(result.overallTip());
    }

    @Test
    void shouldReturnHighScoreWhenRecognizedMatchesTarget() throws Exception {
        List<WordConfidenceDto> wordConfidences = List.of(
                new WordConfidenceDto("hello", 0.99),
                new WordConfidenceDto("world", 0.98)
        );

        PronunciationFeedbackDto result = useCase.execute("hello world", "hello world", wordConfidences);

        assertNotNull(result);
        assertNotNull(result.overallTip());
    }

    @Test
    void shouldPropagateAiExceptionWhenAiFails() {
        aiPort.setShouldThrowAiException(true);
        List<WordConfidenceDto> wordConfidences = List.of(new WordConfidenceDto("test", 0.8));

        assertThrows(PronunciationAiException.class,
                () -> useCase.execute("test", "test", wordConfidences));
    }
}
