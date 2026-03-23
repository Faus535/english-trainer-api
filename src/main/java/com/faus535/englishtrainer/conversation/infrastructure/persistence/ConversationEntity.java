package com.faus535.englishtrainer.conversation.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.conversation.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations")
class ConversationEntity implements Persistable<UUID> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 2)
    private String level;

    @Column
    private String topic;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "evaluation_json", columnDefinition = "TEXT")
    private String evaluationJson;

    @Column(name = "goals_json", columnDefinition = "TEXT")
    private String goalsJson;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<ConversationTurnEntity> turnEntities = new ArrayList<>();

    protected ConversationEntity() {}

    static ConversationEntity fromAggregate(Conversation aggregate) {
        ConversationEntity entity = new ConversationEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId();
        entity.level = aggregate.level().value();
        entity.topic = aggregate.topic();
        entity.status = aggregate.status().value();
        entity.summary = aggregate.summary();
        entity.evaluationJson = serializeEvaluation(aggregate.evaluation());
        entity.goalsJson = serializeGoals(aggregate.goals());
        entity.startedAt = aggregate.startedAt();
        entity.endedAt = aggregate.endedAt();
        entity.turnEntities = aggregate.turns().stream()
                .map(turn -> ConversationTurnEntity.fromTurn(turn, entity))
                .toList();
        return entity;
    }

    Conversation toAggregate() {
        List<ConversationTurn> turns = turnEntities.stream()
                .map(ConversationTurnEntity::toTurn)
                .toList();
        return Conversation.reconstitute(
                new ConversationId(id), userId, new ConversationLevel(level),
                topic, ConversationStatus.fromString(status), summary,
                deserializeEvaluation(evaluationJson), deserializeGoals(goalsJson),
                startedAt, endedAt, turns);
    }

    void markAsExisting() {
        this.isNew = false;
    }

    void updateFrom(Conversation aggregate) {
        this.status = aggregate.status().value();
        this.summary = aggregate.summary();
        this.endedAt = aggregate.endedAt();
        this.evaluationJson = serializeEvaluation(aggregate.evaluation());
        this.goalsJson = serializeGoals(aggregate.goals());

        List<UUID> existingTurnIds = turnEntities.stream()
                .map(ConversationTurnEntity::getId)
                .toList();

        for (ConversationTurn turn : aggregate.turns()) {
            if (!existingTurnIds.contains(turn.id().value())) {
                turnEntities.add(ConversationTurnEntity.fromTurn(turn, this));
            }
        }
    }

    private static String serializeEvaluation(ConversationEvaluation evaluation) {
        if (evaluation == null) return null;
        try {
            return MAPPER.writeValueAsString(evaluation);
        } catch (Exception e) {
            return null;
        }
    }

    private static ConversationEvaluation deserializeEvaluation(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, ConversationEvaluation.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static String serializeGoals(List<ConversationGoal> goals) {
        if (goals == null || goals.isEmpty()) return null;
        try {
            return MAPPER.writeValueAsString(goals);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<ConversationGoal> deserializeGoals(String json) {
        if (json == null || json.isBlank()) return List.of();
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
