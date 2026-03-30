package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.PhonemeDailyAssignment;
import com.faus535.englishtrainer.phonetics.domain.PhonemeDailyAssignmentId;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "phoneme_daily_assignments")
class PhonemeDailyAssignmentEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "phoneme_id", nullable = false)
    private UUID phonemeId;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected PhonemeDailyAssignmentEntity() {}

    static PhonemeDailyAssignmentEntity fromAggregate(PhonemeDailyAssignment aggregate) {
        PhonemeDailyAssignmentEntity entity = new PhonemeDailyAssignmentEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.phonemeId = aggregate.phonemeId().value();
        entity.assignedDate = aggregate.assignedDate();
        entity.completed = aggregate.completed();
        entity.completedAt = aggregate.completedAt();
        return entity;
    }

    PhonemeDailyAssignment toAggregate() {
        return PhonemeDailyAssignment.reconstitute(
                new PhonemeDailyAssignmentId(id),
                new UserProfileId(userId),
                new PhonemeId(phonemeId),
                assignedDate, completed, completedAt
        );
    }

    void markAsExisting() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
