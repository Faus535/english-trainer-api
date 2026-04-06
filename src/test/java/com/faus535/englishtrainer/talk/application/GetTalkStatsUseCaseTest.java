package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetTalkStatsUseCaseTest {

    private static final UUID USER_A = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USER_B = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private InMemoryTalkConversationRepository repository;
    private GetTalkStatsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTalkConversationRepository();
        useCase = new GetTalkStatsUseCase(repository);
    }

    @Test
    void returnsZeroStatsWhenNoConversations() {
        TalkStats stats = useCase.execute(USER_A);

        assertEquals(0, stats.totalConversations());
        assertEquals(0, stats.completedConversations());
        assertEquals(0, stats.totalMessages());
        assertEquals(0.0, stats.averageScore());
    }

    @Test
    void countsOnlyCompletedConversations() {
        TalkConversation completed = TalkConversationMother.completed();
        TalkConversation active = TalkConversationMother.active();
        repository.save(completed);
        repository.save(active);

        TalkStats stats = useCase.execute(USER_A);

        assertEquals(2, stats.totalConversations());
        assertEquals(1, stats.completedConversations());
    }

    @Test
    void calculatesAverageScoreFromCompletedConversations() {
        TalkConversation completed = TalkConversationMother.completed();
        repository.save(completed);

        TalkStats stats = useCase.execute(USER_A);

        assertEquals(78.0, stats.averageScore());
    }

    @Test
    void ignoresConversationsFromOtherUsers() {
        TalkConversation completed = TalkConversationMother.completed();
        repository.save(completed);

        TalkStats stats = useCase.execute(USER_B);

        assertEquals(0, stats.totalConversations());
        assertEquals(0, stats.completedConversations());
        assertEquals(0.0, stats.averageScore());
    }

    @Test
    void sumsTotalMessagesAcrossConversations() {
        TalkConversation conv1 = TalkConversationMother.withMessages(4);
        TalkConversation conv2 = TalkConversationMother.withMessages(2);
        repository.save(conv1);
        repository.save(conv2);

        TalkStats stats = useCase.execute(USER_A);

        assertEquals(6, stats.totalMessages());
    }
}
