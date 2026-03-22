package com.faus535.englishtrainer.conversation.infrastructure.persistence;

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
                startedAt, endedAt, turns);
    }

    void markAsExisting() {
        this.isNew = false;
    }

    void updateFrom(Conversation aggregate) {
        this.status = aggregate.status().value();
        this.summary = aggregate.summary();
        this.endedAt = aggregate.endedAt();

        List<UUID> existingTurnIds = turnEntities.stream()
                .map(ConversationTurnEntity::getId)
                .toList();

        for (ConversationTurn turn : aggregate.turns()) {
            if (!existingTurnIds.contains(turn.id().value())) {
                turnEntities.add(ConversationTurnEntity.fromTurn(turn, this));
            }
        }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
