package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class AnalyzePronunciationUseCase {

    private final PronunciationAiPort pronunciationAiPort;

    public AnalyzePronunciationUseCase(PronunciationAiPort pronunciationAiPort) {
        this.pronunciationAiPort = pronunciationAiPort;
    }

    @Transactional(readOnly = true)
    public PronunciationAnalysisDto execute(String text, String level) throws PronunciationAiException {
        var result = pronunciationAiPort.analyze(text, level);
        return new PronunciationAnalysisDto(text, result.ipa(), result.syllables(),
                result.stressPattern(), result.tips(), result.commonMistakes(),
                result.minimalPairs(), result.exampleSentences());
    }
}
