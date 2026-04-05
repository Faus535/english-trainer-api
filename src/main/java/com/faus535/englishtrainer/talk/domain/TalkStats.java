package com.faus535.englishtrainer.talk.domain;

public record TalkStats(
        int totalConversations,
        int completedConversations,
        int totalMessages,
        double averageScore
) {}
