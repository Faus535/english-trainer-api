package com.faus535.englishtrainer.conversation.domain;

import java.util.List;

public record ConversationEvaluation(
        int grammarAccuracy,
        int vocabularyRange,
        int fluency,
        int taskCompletion,
        int overallScore,
        String levelDemonstrated,
        List<String> strengths,
        List<String> areasToImprove
) {

    public ConversationEvaluation {
        strengths = strengths != null ? List.copyOf(strengths) : List.of();
        areasToImprove = areasToImprove != null ? List.copyOf(areasToImprove) : List.of();
    }

    public static ConversationEvaluation empty() {
        return new ConversationEvaluation(0, 0, 0, 0, 0, "a1", List.of(), List.of());
    }
}
