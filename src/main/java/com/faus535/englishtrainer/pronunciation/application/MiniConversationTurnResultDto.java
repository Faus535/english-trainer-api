package com.faus535.englishtrainer.pronunciation.application;

import java.util.List;

public record MiniConversationTurnResultDto(
        int score,
        List<WordFeedbackDto> wordFeedback,
        String nextPrompt,
        String nextTargetPhrase,
        boolean isComplete) {}
