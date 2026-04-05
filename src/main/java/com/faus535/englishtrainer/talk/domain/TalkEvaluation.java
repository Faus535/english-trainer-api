package com.faus535.englishtrainer.talk.domain;

import java.util.List;

public record TalkEvaluation(
        int grammarAccuracy,
        int vocabularyRange,
        int fluency,
        int taskCompletion,
        int overallScore,
        String levelDemonstrated,
        List<String> strengths,
        List<String> areasToImprove
) {

    public TalkEvaluation {
        strengths = strengths != null ? List.copyOf(strengths) : List.of();
        areasToImprove = areasToImprove != null ? List.copyOf(areasToImprove) : List.of();
    }

    public static TalkEvaluation empty() {
        return new TalkEvaluation(0, 0, 0, 0, 0, "a1", List.of(), List.of());
    }
}
