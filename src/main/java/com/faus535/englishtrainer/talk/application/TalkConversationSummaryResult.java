package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.TalkEvaluation;

import java.util.List;

public sealed interface TalkConversationSummaryResult
        permits TalkConversationSummaryResult.FullSummaryResult, TalkConversationSummaryResult.QuickSummaryResult {

    record FullSummaryResult(String summary, TalkEvaluation evaluation,
                              int turnCount, int errorCount) implements TalkConversationSummaryResult {}

    record QuickSummaryResult(boolean taskCompleted, List<String> top3Corrections,
                               String encouragementNote) implements TalkConversationSummaryResult {}
}
