package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.DrillAttempt;
import com.faus535.englishtrainer.pronunciation.domain.DrillDifficulty;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrill;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pronunciation_drills")
class PronunciationDrillEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String phrase;

    @Column(nullable = false, length = 100)
    private String focus;

    @Column(nullable = false, length = 10)
    private String difficulty;

    @Column(name = "cefr_level", nullable = false, length = 5)
    private String cefrLevel;

    @OneToMany(mappedBy = "drill", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("attemptedAt ASC")
    private List<DrillAttemptEntity> attemptEntities = new ArrayList<>();

    protected PronunciationDrillEntity() {}

    static PronunciationDrillEntity fromAggregate(PronunciationDrill drill) {
        PronunciationDrillEntity entity = new PronunciationDrillEntity();
        entity.id = drill.id().value();
        entity.isNew = true;
        entity.phrase = drill.phrase();
        entity.focus = drill.focus();
        entity.difficulty = drill.difficulty().name();
        entity.cefrLevel = drill.cefrLevel();
        entity.attemptEntities = drill.attempts().stream()
                .map(a -> DrillAttemptEntity.fromDomain(a, entity))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        return entity;
    }

    PronunciationDrill toAggregate() {
        List<DrillAttempt> attempts = attemptEntities.stream()
                .map(DrillAttemptEntity::toDomain)
                .toList();
        return PronunciationDrill.reconstitute(
                new PronunciationDrillId(id), phrase, focus,
                DrillDifficulty.valueOf(difficulty), cefrLevel, attempts);
    }

    void updateFrom(PronunciationDrill drill) {
        List<UUID> existingIds = attemptEntities.stream()
                .map(DrillAttemptEntity::getId)
                .toList();

        for (DrillAttempt attempt : drill.attempts()) {
            if (!existingIds.contains(attempt.id())) {
                attemptEntities.add(DrillAttemptEntity.fromDomain(attempt, this));
            }
        }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
