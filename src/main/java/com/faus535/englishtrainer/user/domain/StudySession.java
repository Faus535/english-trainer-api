package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.user.domain.vo.StudyModule;
import com.faus535.englishtrainer.user.domain.vo.StudySessionId;

import java.time.Instant;
import java.util.UUID;

public final class StudySession {

    private final StudySessionId id;
    private final UUID userId;
    private final StudyModule module;
    private final int durationSeconds;
    private final Instant createdAt;

    private StudySession(StudySessionId id, UUID userId, StudyModule module,
                         int durationSeconds, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.module = module;
        this.durationSeconds = durationSeconds;
        this.createdAt = createdAt;
    }

    public static StudySession create(UUID userId, StudyModule module, int durationSeconds) {
        return new StudySession(StudySessionId.generate(), userId, module, durationSeconds, Instant.now());
    }

    public static StudySession reconstitute(StudySessionId id, UUID userId, StudyModule module,
                                            int durationSeconds, Instant createdAt) {
        return new StudySession(id, userId, module, durationSeconds, createdAt);
    }

    public StudySessionId id() { return id; }
    public UUID userId() { return userId; }
    public StudyModule module() { return module; }
    public int durationSeconds() { return durationSeconds; }
    public Instant createdAt() { return createdAt; }
}
