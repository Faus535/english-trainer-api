package com.faus535.englishtrainer.conversation.domain;

import java.util.List;

public record LevelProfile(
        String level,
        String sentenceLength,
        int maxSentences,
        List<String> allowedTenses,
        List<String> topicExamples,
        List<String> forbiddenTopics,
        String vocabularyBand,
        String errorCorrectionStyle,
        String questionStyle,
        String conversationGoal
) {}
