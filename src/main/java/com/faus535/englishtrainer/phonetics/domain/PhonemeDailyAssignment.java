package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.phonetics.domain.event.PhonemeCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.time.LocalDate;

public final class PhonemeDailyAssignment extends AggregateRoot<PhonemeDailyAssignmentId> {
    private final PhonemeDailyAssignmentId id;
    private final UserProfileId userId;
    private final PhonemeId phonemeId;
    private final LocalDate assignedDate;
    private final boolean completed;
    private final Instant completedAt;

    private PhonemeDailyAssignment(PhonemeDailyAssignmentId id, UserProfileId userId,
                                    PhonemeId phonemeId, LocalDate assignedDate,
                                    boolean completed, Instant completedAt) {
        this.id = id;
        this.userId = userId;
        this.phonemeId = phonemeId;
        this.assignedDate = assignedDate;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public static PhonemeDailyAssignment create(UserProfileId userId, PhonemeId phonemeId,
                                                  LocalDate assignedDate) {
        return new PhonemeDailyAssignment(
            PhonemeDailyAssignmentId.generate(), userId, phonemeId, assignedDate, false, null
        );
    }

    public static PhonemeDailyAssignment reconstitute(PhonemeDailyAssignmentId id, UserProfileId userId,
                                                        PhonemeId phonemeId, LocalDate assignedDate,
                                                        boolean completed, Instant completedAt) {
        return new PhonemeDailyAssignment(id, userId, phonemeId, assignedDate, completed, completedAt);
    }

    public PhonemeDailyAssignment complete() {
        if (this.completed) { return this; }
        PhonemeDailyAssignment completedAssignment = new PhonemeDailyAssignment(
            id, userId, phonemeId, assignedDate, true, Instant.now()
        );
        completedAssignment.registerEvent(new PhonemeCompletedEvent(id, userId, phonemeId));
        return completedAssignment;
    }

    public PhonemeDailyAssignmentId id() { return id; }
    public UserProfileId userId() { return userId; }
    public PhonemeId phonemeId() { return phonemeId; }
    public LocalDate assignedDate() { return assignedDate; }
    public boolean completed() { return completed; }
    public Instant completedAt() { return completedAt; }
}
