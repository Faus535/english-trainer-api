package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationError;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationErrorId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pronunciation_errors")
class PronunciationErrorEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(name = "expected_phoneme", nullable = false, length = 50)
    private String expectedPhoneme;

    @Column(name = "spoken_phoneme", length = 50)
    private String spokenPhoneme;

    @Column(name = "occurrence_count", nullable = false)
    private int occurrenceCount;

    @Column(name = "last_occurred", nullable = false)
    private Instant lastOccurred;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Transient
    private boolean isNew;

    protected PronunciationErrorEntity() {}

    static PronunciationErrorEntity fromAggregate(PronunciationError aggregate) {
        PronunciationErrorEntity entity = new PronunciationErrorEntity();
        entity.id = aggregate.id().value();
        entity.userId = aggregate.userId().value();
        entity.word = aggregate.word();
        entity.expectedPhoneme = aggregate.expectedPhoneme();
        entity.spokenPhoneme = aggregate.spokenPhoneme();
        entity.occurrenceCount = aggregate.occurrenceCount();
        entity.lastOccurred = aggregate.lastOccurred();
        entity.createdAt = aggregate.createdAt();
        entity.isNew = true;
        return entity;
    }

    PronunciationError toAggregate() {
        return PronunciationError.reconstitute(
                new PronunciationErrorId(id),
                new UserProfileId(userId),
                word,
                expectedPhoneme,
                spokenPhoneme,
                occurrenceCount,
                lastOccurred,
                createdAt
        );
    }

    void updateFrom(PronunciationError aggregate) {
        this.spokenPhoneme = aggregate.spokenPhoneme();
        this.occurrenceCount = aggregate.occurrenceCount();
        this.lastOccurred = aggregate.lastOccurred();
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
