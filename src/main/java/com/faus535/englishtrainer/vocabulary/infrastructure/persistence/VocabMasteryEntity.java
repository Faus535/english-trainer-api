package com.faus535.englishtrainer.vocabulary.infrastructure.persistence;

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
@Table(name = "vocab_mastery")
class VocabMasteryEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "vocab_entry_id")
    private UUID vocabEntryId;

    @Column(nullable = false)
    private String word;

    @Column(name = "correct_count", nullable = false)
    private int correctCount;

    @Column(name = "incorrect_count", nullable = false)
    private int incorrectCount;

    @Column(name = "total_attempts", nullable = false)
    private int totalAttempts;

    @Column(nullable = false)
    private String status;

    @Column(name = "source_context", nullable = false)
    private String sourceContext;

    @Column(name = "source_detail")
    private String sourceDetail;

    @Column(name = "last_practiced_at", nullable = false)
    private Instant lastPracticedAt;

    @Column(name = "learned_at")
    private Instant learnedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected VocabMasteryEntity() {}

    static VocabMasteryEntity fromAggregate(UUID id, UUID userId, UUID vocabEntryId, String word,
                                             int correctCount, int incorrectCount, int totalAttempts,
                                             String status, String sourceContext, String sourceDetail,
                                             Instant lastPracticedAt, Instant learnedAt,
                                             Instant createdAt, Instant updatedAt) {
        VocabMasteryEntity entity = new VocabMasteryEntity();
        entity.id = id;
        entity.isNew = true;
        entity.userId = userId;
        entity.vocabEntryId = vocabEntryId;
        entity.word = word;
        entity.correctCount = correctCount;
        entity.incorrectCount = incorrectCount;
        entity.totalAttempts = totalAttempts;
        entity.status = status;
        entity.sourceContext = sourceContext;
        entity.sourceDetail = sourceDetail;
        entity.lastPracticedAt = lastPracticedAt;
        entity.learnedAt = learnedAt;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
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
    UUID vocabEntryId() { return vocabEntryId; }
    String word() { return word; }
    int correctCount() { return correctCount; }
    int incorrectCount() { return incorrectCount; }
    int totalAttempts() { return totalAttempts; }
    String status() { return status; }
    String sourceContext() { return sourceContext; }
    String sourceDetail() { return sourceDetail; }
    Instant lastPracticedAt() { return lastPracticedAt; }
    Instant learnedAt() { return learnedAt; }
    Instant createdAt() { return createdAt; }
    Instant updatedAt() { return updatedAt; }
}
