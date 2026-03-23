package com.faus535.englishtrainer.assessment.domain;

import java.util.List;
import java.util.UUID;

public record TestQuestion(UUID id, String type, String question, List<String> options, String correctAnswer,
                           String level, Double audioSpeed) {

    public TestQuestion(String id, String type, String question, List<String> options, String correctAnswer, String level) {
        this(UUID.nameUUIDFromBytes(id.getBytes()), type, question, options, correctAnswer, level, null);
    }
}
