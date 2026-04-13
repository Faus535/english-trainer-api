package com.faus535.englishtrainer.pronunciation.infrastructure;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;

import java.util.List;

public class StubPronunciationAiPort implements PronunciationAiPort {

    private PronunciationAnalysisResult analysisToReturn = new PronunciationAnalysisResult(
            "/θɔːt/", "thought", "Primary stress on single syllable",
            List.of("The 'th' sound does not exist in Spanish — place tongue between teeth"),
            List.of("Pronouncing 'th' as 'd' or 't'"),
            List.of("thought/taught", "thought/taut"),
            List.of("I thought about it.", "She thought it was funny.")
    );

    private PronunciationFeedbackResult feedbackToReturn = new PronunciationFeedbackResult(
            75,
            List.of(new WordFeedback("thought", "tought", "Place tongue between teeth for 'th'", 70)),
            "Focus on the 'th' sound — it requires tongue between teeth."
    );

    private DrillFeedbackResult drillFeedbackToReturn = new DrillFeedbackResult(
            80, "Good attempt! Focus on the 'th' sound."
    );

    private MiniConversationResult miniConversationToReturn = new MiniConversationResult(
            "Let's practice the 'th' sound. Tell me: what do you think about the weather?",
            "I think the weather is nice today."
    );

    private MiniConversationTurnResult miniConversationTurnToReturn = new MiniConversationTurnResult(
            75,
            List.of(new WordFeedback("think", "tink", "Use tongue between teeth for 'th'", 70)),
            "Great! Now say: I thought about the theory.",
            "I thought about the theory.",
            false
    );

    private boolean shouldThrowAiException = false;

    public void setAnalysisToReturn(PronunciationAnalysisResult result) {
        this.analysisToReturn = result;
    }

    public void setFeedbackToReturn(PronunciationFeedbackResult result) {
        this.feedbackToReturn = result;
    }

    public void setDrillFeedbackToReturn(DrillFeedbackResult result) {
        this.drillFeedbackToReturn = result;
    }

    public void setMiniConversationToReturn(MiniConversationResult result) {
        this.miniConversationToReturn = result;
    }

    public void setMiniConversationTurnToReturn(MiniConversationTurnResult result) {
        this.miniConversationTurnToReturn = result;
    }

    public void setShouldThrowAiException(boolean shouldThrow) {
        this.shouldThrowAiException = shouldThrow;
    }

    @Override
    public PronunciationAnalysisResult analyze(String text, String level) throws PronunciationAiException {
        if (shouldThrowAiException) {
            throw new PronunciationAiException("Stub AI error");
        }
        return analysisToReturn;
    }

    @Override
    public PronunciationFeedbackResult feedback(String targetText, String recognizedText,
            List<WordConfidence> wordConfidences) throws PronunciationAiException {
        if (shouldThrowAiException) {
            throw new PronunciationAiException("Stub AI error");
        }
        return feedbackToReturn;
    }

    @Override
    public DrillFeedbackResult drillFeedback(String drillPhrase, String recognizedText,
            double confidence) throws PronunciationAiException {
        if (shouldThrowAiException) {
            throw new PronunciationAiException("Stub AI error");
        }
        return drillFeedbackToReturn;
    }

    @Override
    public MiniConversationResult startMiniConversation(String focus, String level) throws PronunciationAiException {
        if (shouldThrowAiException) {
            throw new PronunciationAiException("Stub AI error");
        }
        return miniConversationToReturn;
    }

    @Override
    public MiniConversationTurnResult evaluateMiniConversationTurn(String targetPhrase, String recognizedText,
            List<WordConfidence> wordConfidences, String focus, String level) throws PronunciationAiException {
        if (shouldThrowAiException) {
            throw new PronunciationAiException("Stub AI error");
        }
        return miniConversationTurnToReturn;
    }
}
