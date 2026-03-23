package com.faus535.englishtrainer.errorpattern.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.errorpattern.domain.ErrorCategory;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPattern;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPatternId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "error_patterns")
class ErrorPatternEntity implements Persistable<UUID> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private String pattern;

    @Column(name = "examples_json", columnDefinition = "TEXT")
    private String examplesJson;

    @Column(name = "occurrence_count", nullable = false)
    private int occurrenceCount;

    @Column(name = "last_occurred", nullable = false)
    private Instant lastOccurred;

    @Column(nullable = false)
    private boolean resolved;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Transient
    private boolean isNew;

    protected ErrorPatternEntity() {}

    static ErrorPatternEntity fromAggregate(ErrorPattern aggregate) {
        ErrorPatternEntity entity = new ErrorPatternEntity();
        entity.id = aggregate.id().value();
        entity.userId = aggregate.userId();
        entity.category = aggregate.category().name();
        entity.pattern = aggregate.pattern();
        entity.examplesJson = serializeExamples(aggregate.examples());
        entity.occurrenceCount = aggregate.occurrenceCount();
        entity.lastOccurred = aggregate.lastOccurred();
        entity.resolved = aggregate.resolved();
        entity.createdAt = aggregate.createdAt();
        entity.isNew = true;
        return entity;
    }

    ErrorPattern toAggregate() {
        return ErrorPattern.reconstitute(
                new ErrorPatternId(id), userId, ErrorCategory.fromString(category),
                pattern, deserializeExamples(examplesJson), occurrenceCount,
                lastOccurred, resolved, createdAt);
    }

    void updateFrom(ErrorPattern aggregate) {
        this.examplesJson = serializeExamples(aggregate.examples());
        this.occurrenceCount = aggregate.occurrenceCount();
        this.lastOccurred = aggregate.lastOccurred();
        this.resolved = aggregate.resolved();
    }

    void markAsExisting() {
        this.isNew = false;
    }

    private static String serializeExamples(List<String> examples) {
        try { return MAPPER.writeValueAsString(examples); } catch (Exception e) { return "[]"; }
    }

    private static List<String> deserializeExamples(String json) {
        if (json == null) return List.of();
        try { return MAPPER.readValue(json, new TypeReference<>() {}); } catch (Exception e) { return List.of(); }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
