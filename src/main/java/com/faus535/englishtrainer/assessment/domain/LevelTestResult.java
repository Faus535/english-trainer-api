package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.assessment.domain.event.LevelTestCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.util.Map;

public final class LevelTestResult extends AggregateRoot<LevelTestResultId> {

    private final LevelTestResultId id;
    private final UserProfileId userId;
    private final int vocabularyScore;
    private final int grammarScore;
    private final int listeningScore;
    private final int pronunciationScore;
    private final Map<String, String> assignedLevels;
    private final Instant completedAt;

    private LevelTestResult(LevelTestResultId id, UserProfileId userId, int vocabularyScore, int grammarScore,
                            int listeningScore, int pronunciationScore, Map<String, String> assignedLevels,
                            Instant completedAt) {
        this.id = id;
        this.userId = userId;
        this.vocabularyScore = vocabularyScore;
        this.grammarScore = grammarScore;
        this.listeningScore = listeningScore;
        this.pronunciationScore = pronunciationScore;
        this.assignedLevels = Map.copyOf(assignedLevels);
        this.completedAt = completedAt;
    }

    public static LevelTestResult create(UserProfileId userId, int vocabScore, int grammarScore,
                                          int listeningScore, int pronScore, Map<String, String> assignedLevels) {
        LevelTestResult result = new LevelTestResult(
                LevelTestResultId.generate(),
                userId,
                vocabScore,
                grammarScore,
                listeningScore,
                pronScore,
                assignedLevels,
                Instant.now()
        );
        result.registerEvent(new LevelTestCompletedEvent(result.id(), userId));
        return result;
    }

    public static LevelTestResult reconstitute(LevelTestResultId id, UserProfileId userId, int vocabularyScore,
                                                int grammarScore, int listeningScore, int pronunciationScore,
                                                Map<String, String> assignedLevels, Instant completedAt) {
        return new LevelTestResult(id, userId, vocabularyScore, grammarScore, listeningScore,
                pronunciationScore, assignedLevels, completedAt);
    }

    public LevelTestResultId id() { return id; }
    public UserProfileId userId() { return userId; }
    public int vocabularyScore() { return vocabularyScore; }
    public int grammarScore() { return grammarScore; }
    public int listeningScore() { return listeningScore; }
    public int pronunciationScore() { return pronunciationScore; }
    public Map<String, String> assignedLevels() { return assignedLevels; }
    public Instant completedAt() { return completedAt; }
}
