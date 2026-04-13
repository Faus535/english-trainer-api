package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.List;

@UseCase
public class EvaluatePronunciationFeedbackUseCase {

    private final PronunciationAiPort pronunciationAiPort;

    public EvaluatePronunciationFeedbackUseCase(PronunciationAiPort pronunciationAiPort) {
        this.pronunciationAiPort = pronunciationAiPort;
    }

    public PronunciationFeedbackDto execute(String targetText, String recognizedText,
            List<WordConfidenceDto> wordConfidences) throws PronunciationAiException {
        List<PronunciationAiPort.WordConfidence> portConfidences = wordConfidences.stream()
                .map(wc -> new PronunciationAiPort.WordConfidence(wc.word(), wc.confidence()))
                .toList();

        PronunciationAiPort.PronunciationFeedbackResult result =
                pronunciationAiPort.feedback(targetText, recognizedText, portConfidences);

        List<WordFeedbackDto> wordFeedback = result.wordFeedback().stream()
                .map(wf -> new WordFeedbackDto(wf.word(), wf.recognized(), wf.tip(), wf.score()))
                .toList();

        return new PronunciationFeedbackDto(result.score(), wordFeedback, result.overallTip());
    }
}
