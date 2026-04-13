package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.DrillAttempt;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pronunciation_drill_attempts")
class DrillAttemptEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drill_id", nullable = false)
    private PronunciationDrillEntity drill;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int score;

    @Column(name = "recognized_text", columnDefinition = "TEXT")
    private String recognizedText;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    protected DrillAttemptEntity() {}

    static DrillAttemptEntity fromDomain(DrillAttempt attempt, PronunciationDrillEntity drill) {
        DrillAttemptEntity entity = new DrillAttemptEntity();
        entity.id = attempt.id();
        entity.isNew = true;
        entity.drill = drill;
        entity.userId = attempt.userId();
        entity.score = attempt.score();
        entity.recognizedText = attempt.recognizedText();
        entity.attemptedAt = attempt.attemptedAt();
        return entity;
    }

    DrillAttempt toDomain() {
        return new DrillAttempt(id, userId, score, recognizedText, attemptedAt);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
