package com.faus535.englishtrainer.user.infrastructure.persistence;

import com.faus535.englishtrainer.user.domain.StudySession;
import com.faus535.englishtrainer.user.domain.vo.StudyModule;
import com.faus535.englishtrainer.user.domain.vo.StudySessionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "study_sessions")
class StudySessionEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String module;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected StudySessionEntity() {}

    static StudySessionEntity fromAggregate(StudySession session) {
        StudySessionEntity entity = new StudySessionEntity();
        entity.id = session.id().value();
        entity.isNew = true;
        entity.userId = session.userId();
        entity.module = session.module().name();
        entity.durationSeconds = session.durationSeconds();
        entity.createdAt = session.createdAt();
        return entity;
    }

    StudySession toAggregate() {
        return StudySession.reconstitute(
                new StudySessionId(id),
                userId,
                StudyModule.valueOf(module),
                durationSeconds,
                createdAt
        );
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
