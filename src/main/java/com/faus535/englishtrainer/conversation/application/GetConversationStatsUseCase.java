package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@UseCase
public class GetConversationStatsUseCase {

    private final ConversationRepository repository;

    public GetConversationStatsUseCase(ConversationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ConversationStats execute(UUID userId) {
        List<Conversation> conversations = repository.findByUserId(userId);

        if (conversations.isEmpty()) {
            return ConversationStats.empty();
        }

        int totalMessages = 0;
        int totalGrammarCorrections = 0;
        int totalVocabSuggestions = 0;
        int totalPronunciationTips = 0;
        double confidenceSum = 0;
        int confidenceCount = 0;
        Map<String, Integer> topicCounts = new HashMap<>();
        Map<String, Integer> levelCounts = new HashMap<>();
        Map<String, Integer> grammarIssueFrequency = new HashMap<>();

        for (Conversation conv : conversations) {
            String topic = conv.topic() != null ? conv.topic() : "free";
            topicCounts.merge(topic, 1, Integer::sum);
            levelCounts.merge(conv.level().value(), 1, Integer::sum);

            for (ConversationTurn turn : conv.turns()) {
                totalMessages++;

                if (turn.confidence() != null) {
                    confidenceSum += turn.confidence();
                    confidenceCount++;
                }

                TutorFeedback feedback = turn.feedback();
                if (feedback != null) {
                    totalGrammarCorrections += feedback.grammarCorrections().size();
                    totalVocabSuggestions += feedback.vocabularySuggestions().size();
                    totalPronunciationTips += feedback.pronunciationTips().size();

                    for (String correction : feedback.grammarCorrections()) {
                        grammarIssueFrequency.merge(correction, 1, Integer::sum);
                    }
                }
            }
        }

        List<String> frequentGrammarIssues = grammarIssueFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        double avgConfidence = confidenceCount > 0 ? confidenceSum / confidenceCount : 0.0;

        return new ConversationStats(
                conversations.size(), totalMessages,
                totalGrammarCorrections, totalVocabSuggestions, totalPronunciationTips,
                Math.round(avgConfidence * 100.0) / 100.0,
                topicCounts, levelCounts, frequentGrammarIssues);
    }
}
