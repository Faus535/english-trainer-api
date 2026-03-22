package com.faus535.englishtrainer.analytics.infrastructure.persistence;

import com.faus535.englishtrainer.analytics.domain.LevelHistoryEntry;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "level_history")
class LevelHistoryEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private String level;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected LevelHistoryEntity() {}

    static LevelHistoryEntity fromDomain(LevelHistoryEntry entry) {
        LevelHistoryEntity entity = new LevelHistoryEntity();
        entity.id = entry.id();
        entity.isNew = true;
        entity.userId = entry.userId();
        entity.module = entry.module();
        entity.level = entry.level();
        entity.changedAt = entry.changedAt();
        return entity;
    }

    LevelHistoryEntry toDomain() {
        return new LevelHistoryEntry(id, userId, module, level, changedAt);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
