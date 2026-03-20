package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "level_test_results")
class LevelTestResultEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "vocabulary_score", nullable = false)
    private int vocabularyScore;

    @Column(name = "grammar_score", nullable = false)
    private int grammarScore;

    @Column(name = "listening_score", nullable = false)
    private int listeningScore;

    @Column(name = "pronunciation_score", nullable = false)
    private int pronunciationScore;

    @Column(name = "assigned_levels", nullable = false, columnDefinition = "TEXT")
    private String assignedLevels;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    protected LevelTestResultEntity() {}

    static LevelTestResultEntity fromAggregate(UUID id, UUID userId, int vocabularyScore, int grammarScore,
                                               int listeningScore, int pronunciationScore,
                                               String assignedLevels, Instant completedAt) {
        LevelTestResultEntity entity = new LevelTestResultEntity();
        entity.id = id;
        entity.isNew = true;
        entity.userId = userId;
        entity.vocabularyScore = vocabularyScore;
        entity.grammarScore = grammarScore;
        entity.listeningScore = listeningScore;
        entity.pronunciationScore = pronunciationScore;
        entity.assignedLevels = assignedLevels;
        entity.completedAt = completedAt;
        return entity;
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }

    UUID userId() { return userId; }
    int vocabularyScore() { return vocabularyScore; }
    int grammarScore() { return grammarScore; }
    int listeningScore() { return listeningScore; }
    int pronunciationScore() { return pronunciationScore; }
    String assignedLevels() { return assignedLevels; }
    Instant completedAt() { return completedAt; }
}
