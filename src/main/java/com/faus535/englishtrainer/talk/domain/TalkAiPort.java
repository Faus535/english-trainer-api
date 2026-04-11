package com.faus535.englishtrainer.talk.domain;

import com.faus535.englishtrainer.talk.domain.error.TalkAiException;

import java.util.List;

public interface TalkAiPort {

    TalkAiResponse chat(TalkLevel level, TalkScenario scenario, List<TalkMessage> messages,
                         Float confidence) throws TalkAiException;

    String summarize(TalkLevel level, List<TalkMessage> messages) throws TalkAiException;

    TalkEvaluation evaluate(TalkLevel level, List<TalkMessage> messages) throws TalkAiException;

    QuickSummary quickSummarize(List<TalkMessage> messages) throws TalkAiException;

    GrammarFeedback analyzeGrammarAndVocabulary(List<TalkMessage> userMessages) throws TalkAiException;

    record TalkAiResponse(String content, TalkCorrection correction) {}

    record QuickSummary(boolean taskCompleted, List<String> top3Corrections, String encouragementNote) {}

    record GrammarFeedback(List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary) {}
}
