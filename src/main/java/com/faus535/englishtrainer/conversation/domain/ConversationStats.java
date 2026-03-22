package com.faus535.englishtrainer.conversation.domain;

import java.util.List;
import java.util.Map;

public record ConversationStats(
        int totalConversations,
        int totalMessages,
        int totalGrammarCorrections,
        int totalVocabSuggestions,
        int totalPronunciationTips,
        double averageConfidence,
        Map<String, Integer> topicCounts,
        Map<String, Integer> levelCounts,
        List<String> frequentGrammarIssues
) {

    public static ConversationStats empty() {
        return new ConversationStats(0, 0, 0, 0, 0, 0.0, Map.of(), Map.of(), List.of());
    }
}
