package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.GrammarNote;
import com.faus535.englishtrainer.talk.domain.TalkEvaluation;
import com.faus535.englishtrainer.talk.domain.VocabItem;

import java.util.List;

public sealed interface TalkConversationSummaryResult
        permits TalkConversationSummaryResult.FullSummaryResult, TalkConversationSummaryResult.QuickSummaryResult {

    record FullSummaryResult(String summary, TalkEvaluation evaluation,
                              int turnCount, int errorCount,
                              List<GrammarNote> grammarNotes,
                              List<VocabItem> newVocabulary) implements TalkConversationSummaryResult {}

    record QuickSummaryResult(boolean taskCompleted, List<String> top3Corrections,
                               String encouragementNote) implements TalkConversationSummaryResult {}
}
