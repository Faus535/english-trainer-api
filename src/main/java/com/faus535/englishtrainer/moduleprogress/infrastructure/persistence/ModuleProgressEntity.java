package com.faus535.englishtrainer.moduleprogress.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "module_progress")
class ModuleProgressEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(nullable = false)
    private String level;

    @Column(name = "current_unit", nullable = false)
    private int currentUnit;

    @Column(name = "completed_units")
    private String completedUnits;

    @Column
    private String scores;

    protected ModuleProgressEntity() {}

    static ModuleProgressEntity fromAggregate(UUID id, UUID userId, String moduleName, String level,
                                              int currentUnit, String completedUnits, String scores) {
        ModuleProgressEntity entity = new ModuleProgressEntity();
        entity.id = id;
        entity.isNew = true;
        entity.userId = userId;
        entity.moduleName = moduleName;
        entity.level = level;
        entity.currentUnit = currentUnit;
        entity.completedUnits = completedUnits;
        entity.scores = scores;
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
    String moduleName() { return moduleName; }
    String level() { return level; }
    int currentUnit() { return currentUnit; }
    String completedUnits() { return completedUnits; }
    String scores() { return scores; }
}
