package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.*;
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

    static PhonemeDailyAssignmentEntity fromAggregate(PhonemeDailyAssignment agg, boolean isNewEntity) {
        PhonemeDailyAssignmentEntity e = new PhonemeDailyAssignmentEntity();
        e.id = agg.id().value();
        e.isNew = isNewEntity;
        e.userId = agg.userId().value();
        e.phonemeId = agg.phonemeId().value();
        e.assignedDate = agg.assignedDate();
        e.completed = agg.completed();
        e.completedAt = agg.completedAt();
        return e;
    }

    PhonemeDailyAssignment toAggregate() {
        return PhonemeDailyAssignment.reconstitute(
            new PhonemeDailyAssignmentId(id), new UserProfileId(userId),
            new PhonemeId(phonemeId), assignedDate, completed, completedAt
        );
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
