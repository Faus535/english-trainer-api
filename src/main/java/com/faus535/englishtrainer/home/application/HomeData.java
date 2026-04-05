package com.faus535.englishtrainer.home.application;

public record HomeData(
        String suggestedAction,
        TalkSummary talk,
        ImmerseSummary immerse,
        ReviewSummary review
) {

    public record TalkSummary(boolean hasActiveConversation, int totalCompleted, Integer lastScore) {}
    public record ImmerseSummary(int recentContentCount, String lastContentTitle) {}
    public record ReviewSummary(int dueToday, int streak) {}
}
