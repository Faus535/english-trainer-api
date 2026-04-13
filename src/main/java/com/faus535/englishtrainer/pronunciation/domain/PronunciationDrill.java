package com.faus535.englishtrainer.pronunciation.domain;

import com.faus535.englishtrainer.pronunciation.domain.event.PronunciationDrillCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class PronunciationDrill extends AggregateRoot<PronunciationDrillId> {

    private final PronunciationDrillId id;
    private final String phrase;
    private final String focus;
    private final DrillDifficulty difficulty;
    private final String cefrLevel;
    private final List<DrillAttempt> attempts;

    private PronunciationDrill(PronunciationDrillId id, String phrase, String focus,
            DrillDifficulty difficulty, String cefrLevel, List<DrillAttempt> attempts) {
        this.id = id;
        this.phrase = phrase;
        this.focus = focus;
        this.difficulty = difficulty;
        this.cefrLevel = cefrLevel;
        this.attempts = Collections.unmodifiableList(new ArrayList<>(attempts));
    }

    public static PronunciationDrill reconstitute(PronunciationDrillId id, String phrase, String focus,
            DrillDifficulty difficulty, String cefrLevel, List<DrillAttempt> attempts) {
        return new PronunciationDrill(id, phrase, focus, difficulty, cefrLevel, attempts);
    }

    public PronunciationDrill addAttempt(UUID userId, int score, String recognizedText) {
        DrillAttempt attempt = new DrillAttempt(UUID.randomUUID(), userId, score, recognizedText, Instant.now());
        List<DrillAttempt> newAttempts = new ArrayList<>(attempts);
        newAttempts.add(attempt);
        PronunciationDrill updated = new PronunciationDrill(id, phrase, focus, difficulty, cefrLevel, newAttempts);
        if (score >= 70) {
            int streak = updated.perfectStreakFor(userId);
            updated.registerEvent(new PronunciationDrillCompletedEvent(id, userId, score, streak));
        }
        return updated;
    }

    public int perfectStreakFor(UUID userId) {
        List<DrillAttempt> userAttempts = attempts.stream()
                .filter(a -> a.userId().equals(userId))
                .toList();
        int streak = 0;
        for (int i = userAttempts.size() - 1; i >= 0; i--) {
            if (userAttempts.get(i).score() >= 90) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    public PronunciationDrillId id() { return id; }
    public String phrase() { return phrase; }
    public String focus() { return focus; }
    public DrillDifficulty difficulty() { return difficulty; }
    public String cefrLevel() { return cefrLevel; }
    public List<DrillAttempt> attempts() { return attempts; }
}
