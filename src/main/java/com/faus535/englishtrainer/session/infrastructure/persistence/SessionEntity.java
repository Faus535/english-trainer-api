package com.faus535.englishtrainer.session.infrastructure.persistence;

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
@Table(name = "sessions")
class SessionEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String mode;

    @Column(name = "session_type", nullable = false)
    private String sessionType;

    @Column(name = "listening_module")
    private String listeningModule;

    @Column(name = "secondary_module")
    private String secondaryModule;

    @Column(name = "integrator_theme")
    private String integratorTheme;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "blocks_data")
    private String blocksData;

    @Column(name = "exercises_data")
    private String exercisesData;

    protected SessionEntity() {}

    static SessionEntity fromAggregate(UUID id, UUID userId, String mode, String sessionType,
                                       String listeningModule, String secondaryModule, String integratorTheme,
                                       boolean completed, Instant startedAt, Instant completedAt,
                                       Integer durationMinutes, String blocksData, String exercisesData) {
        SessionEntity entity = new SessionEntity();
        entity.id = id;
        entity.isNew = true;
        entity.userId = userId;
        entity.mode = mode;
        entity.sessionType = sessionType;
        entity.listeningModule = listeningModule;
        entity.secondaryModule = secondaryModule;
        entity.integratorTheme = integratorTheme;
        entity.completed = completed;
        entity.startedAt = startedAt;
        entity.completedAt = completedAt;
        entity.durationMinutes = durationMinutes;
        entity.blocksData = blocksData;
        entity.exercisesData = exercisesData;
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
    String mode() { return mode; }
    String sessionType() { return sessionType; }
    String listeningModule() { return listeningModule; }
    String secondaryModule() { return secondaryModule; }
    String integratorTheme() { return integratorTheme; }
    boolean completed() { return completed; }
    Instant startedAt() { return startedAt; }
    Instant completedAt() { return completedAt; }
    Integer durationMinutes() { return durationMinutes; }
    String blocksData() { return blocksData; }
    String exercisesData() { return exercisesData; }
}
