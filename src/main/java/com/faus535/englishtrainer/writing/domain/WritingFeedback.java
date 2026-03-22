package com.faus535.englishtrainer.writing.domain;

import java.util.List;

public record WritingFeedback(
        double grammarScore,
        double coherenceScore,
        double vocabularyScore,
        double overallScore,
        String levelAssessment,
        String generalFeedback,
        List<String> corrections
) {

    public static WritingFeedback empty() {
        return new WritingFeedback(0, 0, 0, 0, "a1", "Unable to evaluate", List.of());
    }
}
