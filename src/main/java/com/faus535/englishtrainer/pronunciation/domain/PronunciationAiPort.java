package com.faus535.englishtrainer.pronunciation.domain;

import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;

import java.util.List;

public interface PronunciationAiPort {

    PronunciationAnalysisResult analyze(String text, String level) throws PronunciationAiException;

    PronunciationFeedbackResult feedback(String targetText, String recognizedText,
            List<WordConfidence> wordConfidences) throws PronunciationAiException;

    DrillFeedbackResult drillFeedback(String drillPhrase, String recognizedText,
            double confidence) throws PronunciationAiException;

    MiniConversationResult startMiniConversation(String focus, String level) throws PronunciationAiException;

    MiniConversationTurnResult evaluateMiniConversationTurn(String targetPhrase, String recognizedText,
            List<WordConfidence> wordConfidences, String focus, String level) throws PronunciationAiException;

    record PronunciationAnalysisResult(String ipa, String syllables, String stressPattern,
            List<String> tips, List<String> commonMistakes, List<String> minimalPairs,
            List<String> exampleSentences) {}

    record WordConfidence(String word, double confidence) {}

    record WordFeedback(String word, String recognized, String tip, int score) {}

    record PronunciationFeedbackResult(int score, List<WordFeedback> wordFeedback, String overallTip) {}

    record DrillFeedbackResult(int score, String feedback) {}

    record MiniConversationResult(String prompt, String targetPhrase) {}

    record MiniConversationTurnResult(int score, List<WordFeedback> wordFeedback,
            String nextPrompt, String nextTargetPhrase, boolean isComplete) {}
}
