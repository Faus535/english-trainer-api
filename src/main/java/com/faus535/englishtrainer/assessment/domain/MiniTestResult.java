package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class MiniTestResult extends AggregateRoot<MiniTestResultId> {

    private final MiniTestResultId id;
    private final UserProfileId userId;
    private final String moduleName;
    private final String level;
    private final int score;
    private final int totalQuestions;
    private final int correctAnswers;
    private final String recommendation;
    private final Instant completedAt;

    private MiniTestResult(MiniTestResultId id, UserProfileId userId, String moduleName, String level,
                           int score, int totalQuestions, int correctAnswers, String recommendation,
                           Instant completedAt) {
        this.id = id;
        this.userId = userId;
        this.moduleName = moduleName;
        this.level = level;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.recommendation = recommendation;
        this.completedAt = completedAt;
    }

    public static MiniTestResult create(UserProfileId userId, String moduleName, String level,
                                         int score, int totalQuestions, int correctAnswers) {
        String recommendation;
        if (score < 60) {
            recommendation = "demote";
        } else if (score > 85) {
            recommendation = "promote";
        } else {
            recommendation = "maintain";
        }
        return new MiniTestResult(
                MiniTestResultId.generate(),
                userId,
                moduleName,
                level,
                score,
                totalQuestions,
                correctAnswers,
                recommendation,
                Instant.now()
        );
    }

    public static MiniTestResult reconstitute(MiniTestResultId id, UserProfileId userId, String moduleName,
                                               String level, int score, int totalQuestions, int correctAnswers,
                                               String recommendation, Instant completedAt) {
        return new MiniTestResult(id, userId, moduleName, level, score, totalQuestions, correctAnswers,
                recommendation, completedAt);
    }

    public MiniTestResultId id() { return id; }
    public UserProfileId userId() { return userId; }
    public String moduleName() { return moduleName; }
    public String level() { return level; }
    public int score() { return score; }
    public int totalQuestions() { return totalQuestions; }
    public int correctAnswers() { return correctAnswers; }
    public String recommendation() { return recommendation; }
    public Instant completedAt() { return completedAt; }
}
