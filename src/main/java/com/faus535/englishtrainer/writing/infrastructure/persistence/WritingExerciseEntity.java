package com.faus535.englishtrainer.writing.infrastructure.persistence;

import com.faus535.englishtrainer.writing.domain.WritingExercise;
import com.faus535.englishtrainer.writing.domain.WritingExerciseId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "writing_exercises")
class WritingExerciseEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(nullable = false)
    private String level;

    private String topic;

    @Column(name = "min_words")
    private int minWords;

    @Column(name = "max_words")
    private int maxWords;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected WritingExerciseEntity() {}

    static WritingExerciseEntity fromAggregate(WritingExercise aggregate) {
        WritingExerciseEntity entity = new WritingExerciseEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.prompt = aggregate.prompt();
        entity.level = aggregate.level();
        entity.topic = aggregate.topic();
        entity.minWords = aggregate.minWords();
        entity.maxWords = aggregate.maxWords();
        entity.createdAt = aggregate.createdAt();
        return entity;
    }

    WritingExercise toAggregate() {
        return WritingExercise.reconstitute(new WritingExerciseId(id), prompt, level, topic, minWords, maxWords, createdAt);
    }

    void markAsExisting() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
