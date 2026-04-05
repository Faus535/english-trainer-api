package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.TalkConversation;
import com.faus535.englishtrainer.talk.domain.TalkConversationRepository;
import com.faus535.englishtrainer.talk.domain.TalkStats;
import com.faus535.englishtrainer.talk.domain.TalkStatus;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetTalkStatsUseCase {

    private final TalkConversationRepository repository;

    public GetTalkStatsUseCase(TalkConversationRepository repository) {
        this.repository = repository;
    }

    public TalkStats execute(UUID userId) {
        List<TalkConversation> conversations = repository.findByUserId(userId);

        int total = conversations.size();
        int completed = (int) conversations.stream()
                .filter(c -> c.status() == TalkStatus.COMPLETED)
                .count();
        int totalMessages = conversations.stream()
                .mapToInt(c -> c.messages().size())
                .sum();
        double avgScore = conversations.stream()
                .filter(c -> c.evaluation() != null)
                .mapToInt(c -> c.evaluation().overallScore())
                .average()
                .orElse(0.0);

        return new TalkStats(total, completed, totalMessages, avgScore);
    }
}
