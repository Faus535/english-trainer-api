package com.faus535.englishtrainer.article.domain;

import java.util.UUID;

public record QuestionWithAnswer(
        UUID questionId,
        String questionText,
        boolean answered,
        String answer
) {}
