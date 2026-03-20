package com.faus535.englishtrainer.assessment.domain;

import java.util.List;

public record TestQuestion(String id, String type, String question, List<String> options, String correctAnswer, String level) {
}
