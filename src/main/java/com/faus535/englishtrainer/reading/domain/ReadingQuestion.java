package com.faus535.englishtrainer.reading.domain;

import java.util.List;
import java.util.UUID;

public record ReadingQuestion(
        UUID id,
        String question,
        List<String> options,
        int correctAnswer,
        String explanation
) {

    public static ReadingQuestion create(String question, List<String> options,
                                          int correctAnswer, String explanation) {
        return new ReadingQuestion(UUID.randomUUID(), question, List.copyOf(options), correctAnswer, explanation);
    }

    public static ReadingQuestion reconstitute(UUID id, String question, List<String> options,
                                                int correctAnswer, String explanation) {
        return new ReadingQuestion(id, question, List.copyOf(options), correctAnswer, explanation);
    }
}
