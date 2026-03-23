package com.faus535.englishtrainer.tutorerror.infrastructure.persistence;

import com.faus535.englishtrainer.tutorerror.domain.TutorError;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tutor_errors")
class TutorErrorEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "error_type", nullable = false, length = 20)
    private String errorType;

    @Column(name = "original_text", nullable = false, length = 500)
    private String originalText;

    @Column(name = "corrected_text", nullable = false, length = 500)
    private String correctedText;

    @Column(length = 255)
    private String rule;

    @Column(name = "occurrence_count", nullable = false)
    private int occurrenceCount;

    @Column(name = "first_seen", nullable = false)
    private Instant firstSeen;

    @Column(name = "last_seen", nullable = false)
    private Instant lastSeen;

    protected TutorErrorEntity() {}

    static TutorErrorEntity fromAggregate(TutorError aggregate) {
        TutorErrorEntity entity = new TutorErrorEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.errorType = aggregate.errorType();
        entity.originalText = aggregate.originalText();
        entity.correctedText = aggregate.correctedText();
        entity.rule = aggregate.rule();
        entity.occurrenceCount = aggregate.occurrenceCount();
        entity.firstSeen = aggregate.firstSeen();
        entity.lastSeen = aggregate.lastSeen();
        return entity;
    }

    TutorError toAggregate() {
        return TutorError.reconstitute(
                new TutorErrorId(id),
                new UserProfileId(userId),
                errorType,
                originalText,
                correctedText,
                rule,
                occurrenceCount,
                firstSeen,
                lastSeen);
    }

    void updateFrom(TutorError aggregate) {
        this.occurrenceCount = aggregate.occurrenceCount();
        this.lastSeen = aggregate.lastSeen();
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
