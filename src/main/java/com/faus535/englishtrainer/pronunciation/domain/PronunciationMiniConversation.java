package com.faus535.englishtrainer.pronunciation.domain;

import com.faus535.englishtrainer.pronunciation.domain.event.PronunciationMiniConversationCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class PronunciationMiniConversation extends AggregateRoot<PronunciationMiniConversationId> {

    public static final int MAX_TURNS = 5;

    private final PronunciationMiniConversationId id;
    private final UUID userId;
    private final String focus;
    private final String level;
    private final MiniConversationStatus status;
    private final String currentPrompt;
    private final String currentTargetPhrase;
    private final List<MiniConversationTurn> turns;
    private final Instant startedAt;
    private final Instant completedAt;

    private PronunciationMiniConversation(PronunciationMiniConversationId id, UUID userId, String focus,
            String level, MiniConversationStatus status, String currentPrompt, String currentTargetPhrase,
            List<MiniConversationTurn> turns, Instant startedAt, Instant completedAt) {
        this.id = id;
        this.userId = userId;
        this.focus = focus;
        this.level = level;
        this.status = status;
        this.currentPrompt = currentPrompt;
        this.currentTargetPhrase = currentTargetPhrase;
        this.turns = Collections.unmodifiableList(new ArrayList<>(turns));
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public static PronunciationMiniConversation start(UUID userId, String focus, String level,
            String initialPrompt, String initialTargetPhrase) {
        return new PronunciationMiniConversation(
                PronunciationMiniConversationId.generate(), userId, focus, level,
                MiniConversationStatus.ACTIVE, initialPrompt, initialTargetPhrase,
                List.of(), Instant.now(), null);
    }

    public static PronunciationMiniConversation reconstitute(PronunciationMiniConversationId id, UUID userId,
            String focus, String level, MiniConversationStatus status, String currentPrompt,
            String currentTargetPhrase, List<MiniConversationTurn> turns, Instant startedAt, Instant completedAt) {
        return new PronunciationMiniConversation(id, userId, focus, level, status,
                currentPrompt, currentTargetPhrase, turns, startedAt, completedAt);
    }

    public PronunciationMiniConversation evaluateTurn(String recognizedText, int score,
            String nextPrompt, String nextTargetPhrase) {
        MiniConversationTurn turn = new MiniConversationTurn(
                UUID.randomUUID(), turns.size() + 1, currentTargetPhrase,
                recognizedText, score, Instant.now());

        List<MiniConversationTurn> newTurns = new ArrayList<>(turns);
        newTurns.add(turn);

        boolean complete = newTurns.size() >= MAX_TURNS;
        MiniConversationStatus newStatus = complete ? MiniConversationStatus.COMPLETED : MiniConversationStatus.ACTIVE;
        Instant completedAt = complete ? Instant.now() : null;

        PronunciationMiniConversation updated = new PronunciationMiniConversation(
                id, userId, focus, level, newStatus,
                complete ? null : nextPrompt,
                complete ? null : nextTargetPhrase,
                newTurns, startedAt, completedAt);

        if (complete) {
            updated.registerEvent(new PronunciationMiniConversationCompletedEvent(id, userId, updated.averageScore()));
        }

        return updated;
    }

    public boolean isComplete() {
        return status == MiniConversationStatus.COMPLETED;
    }

    public int averageScore() {
        if (turns.isEmpty()) return 0;
        return (int) turns.stream().mapToInt(MiniConversationTurn::score).average().orElse(0);
    }

    public PronunciationMiniConversationId id() { return id; }
    public UUID userId() { return userId; }
    public String focus() { return focus; }
    public String level() { return level; }
    public MiniConversationStatus status() { return status; }
    public String currentPrompt() { return currentPrompt; }
    public String currentTargetPhrase() { return currentTargetPhrase; }
    public List<MiniConversationTurn> turns() { return turns; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
}
