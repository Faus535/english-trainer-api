package com.faus535.englishtrainer.exercise.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.exercise.domain.ConversationExercise;
import com.faus535.englishtrainer.exercise.domain.ConversationExerciseId;
import com.faus535.englishtrainer.exercise.domain.Exercise;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversation_exercises")
class ConversationExerciseEntity implements Persistable<UUID> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "exercises_json", columnDefinition = "TEXT", nullable = false)
    private String exercisesJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @jakarta.persistence.Transient
    private boolean isNew;

    protected ConversationExerciseEntity() {}

    static ConversationExerciseEntity fromAggregate(ConversationExercise aggregate) {
        ConversationExerciseEntity entity = new ConversationExerciseEntity();
        entity.id = aggregate.id().value();
        entity.conversationId = aggregate.conversationId();
        entity.userId = aggregate.userId();
        entity.exercisesJson = serializeExercises(aggregate.exercises());
        entity.createdAt = aggregate.createdAt();
        entity.isNew = true;
        return entity;
    }

    ConversationExercise toAggregate() {
        return ConversationExercise.reconstitute(
                new ConversationExerciseId(id), conversationId, userId,
                deserializeExercises(exercisesJson), createdAt);
    }

    private static String serializeExercises(List<Exercise> exercises) {
        try {
            return MAPPER.writeValueAsString(exercises);
        } catch (Exception e) {
            return "[]";
        }
    }

    private static List<Exercise> deserializeExercises(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
