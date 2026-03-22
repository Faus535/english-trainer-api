package com.faus535.englishtrainer.conversation.domain;

import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.event.ConversationCompletedEvent;
import com.faus535.englishtrainer.conversation.domain.event.ConversationStartedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class Conversation extends AggregateRoot<ConversationId> {

    private final ConversationId id;
    private final UUID userId;
    private final ConversationLevel level;
    private final String topic;
    private final ConversationStatus status;
    private final String summary;
    private final Instant startedAt;
    private final Instant endedAt;
    private final List<ConversationTurn> turns;

    private Conversation(ConversationId id, UUID userId, ConversationLevel level, String topic,
                         ConversationStatus status, String summary, Instant startedAt, Instant endedAt,
                         List<ConversationTurn> turns) {
        this.id = id;
        this.userId = userId;
        this.level = level;
        this.topic = topic;
        this.status = status;
        this.summary = summary;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.turns = Collections.unmodifiableList(new ArrayList<>(turns));
    }

    public static Conversation start(UUID userId, ConversationLevel level, String topic) {
        Conversation conversation = new Conversation(
                ConversationId.generate(), userId, level, topic,
                ConversationStatus.ACTIVE, null, Instant.now(), null, List.of());
        conversation.registerEvent(new ConversationStartedEvent(conversation.id(), userId));
        return conversation;
    }

    public static Conversation reconstitute(ConversationId id, UUID userId, ConversationLevel level,
                                             String topic, ConversationStatus status, String summary,
                                             Instant startedAt, Instant endedAt, List<ConversationTurn> turns) {
        return new Conversation(id, userId, level, topic, status, summary, startedAt, endedAt, turns);
    }

    public Conversation addTurn(ConversationTurn turn) throws ConversationAlreadyEndedException {
        if (status == ConversationStatus.COMPLETED) {
            throw new ConversationAlreadyEndedException(id);
        }
        List<ConversationTurn> newTurns = new ArrayList<>(turns);
        newTurns.add(turn);
        return new Conversation(id, userId, level, topic, status, summary, startedAt, endedAt, newTurns);
    }

    public Conversation end(String summary) throws ConversationAlreadyEndedException {
        if (status == ConversationStatus.COMPLETED) {
            throw new ConversationAlreadyEndedException(id);
        }
        Conversation ended = new Conversation(id, userId, level, topic,
                ConversationStatus.COMPLETED, summary, startedAt, Instant.now(), turns);
        ended.registerEvent(new ConversationCompletedEvent(id, userId, turns.size()));
        return ended;
    }

    public List<ConversationTurn> recentTurns(int maxTurns) {
        if (turns.size() <= maxTurns) {
            return turns;
        }
        return turns.subList(turns.size() - maxTurns, turns.size());
    }

    public ConversationId id() { return id; }
    public UUID userId() { return userId; }
    public ConversationLevel level() { return level; }
    public String topic() { return topic; }
    public ConversationStatus status() { return status; }
    public String summary() { return summary; }
    public Instant startedAt() { return startedAt; }
    public Instant endedAt() { return endedAt; }
    public List<ConversationTurn> turns() { return turns; }
}
