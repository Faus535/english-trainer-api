package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.ConversationRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@UseCase
public class SuggestTopicsUseCase {

    private static final List<String> ALL_TOPICS = List.of(
            "free", "job_interview", "restaurant", "travel", "shopping",
            "daily_life", "healthcare", "education", "technology", "sports",
            "music", "movies", "cooking", "environment", "business"
    );

    private final ConversationRepository repository;

    public SuggestTopicsUseCase(ConversationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<String> execute(UUID userId) {
        List<Conversation> conversations = repository.findByUserId(userId);

        Map<String, Integer> topicUsage = new HashMap<>();
        for (Conversation conv : conversations) {
            String topic = conv.topic() != null ? conv.topic() : "free";
            topicUsage.merge(topic, 1, Integer::sum);
        }

        // Suggest topics the user hasn't tried yet, plus some they haven't used recently
        List<String> suggestions = new ArrayList<>();

        // First: untried topics
        for (String topic : ALL_TOPICS) {
            if (!topicUsage.containsKey(topic)) {
                suggestions.add(topic);
            }
        }

        // Then: least-used topics they've tried
        topicUsage.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .filter(t -> !suggestions.contains(t))
                .forEach(suggestions::add);

        return suggestions.stream().limit(5).toList();
    }
}
