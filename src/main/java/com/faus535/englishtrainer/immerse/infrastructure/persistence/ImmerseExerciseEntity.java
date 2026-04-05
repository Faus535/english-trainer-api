package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.immerse.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "immerse_exercises")
class ImmerseExerciseEntity implements Persistable<UUID> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @Column(name = "content_id", nullable = false)
    private UUID contentId;

    @Column(name = "exercise_type", nullable = false, length = 50)
    private String exerciseType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Transient
    private boolean isNew = true;

    protected ImmerseExerciseEntity() {}

    static ImmerseExerciseEntity fromDomain(ImmerseExercise exercise) {
        ImmerseExerciseEntity entity = new ImmerseExerciseEntity();
        entity.id = exercise.id().value();
        entity.contentId = exercise.contentId().value();
        entity.exerciseType = exercise.exerciseType().name();
        entity.question = exercise.question();
        entity.correctAnswer = exercise.correctAnswer();
        entity.options = serializeOptions(exercise.options());
        entity.orderIndex = exercise.orderIndex();
        return entity;
    }

    ImmerseExercise toDomain() {
        return new ImmerseExercise(
                new ImmerseExerciseId(id), new ImmerseContentId(contentId),
                ExerciseType.valueOf(exerciseType), question, correctAnswer,
                deserializeOptions(options), orderIndex);
    }

    private static String serializeOptions(List<String> opts) {
        if (opts == null || opts.isEmpty()) return null;
        try { return MAPPER.writeValueAsString(opts); } catch (Exception e) { return null; }
    }

    private static List<String> deserializeOptions(String json) {
        if (json == null || json.isBlank()) return List.of();
        try { return MAPPER.readValue(json, new TypeReference<>() {}); } catch (Exception e) { return List.of(); }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
