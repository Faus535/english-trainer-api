package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pronunciation_mini_conversations")
class PronunciationMiniConversationEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String focus;

    @Column(nullable = false, length = 5)
    private String level;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "current_prompt", columnDefinition = "TEXT")
    private String currentPrompt;

    @Column(name = "current_target_phrase", columnDefinition = "TEXT")
    private String currentTargetPhrase;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("turnNumber ASC")
    private List<MiniConversationTurnEntity> turnEntities = new ArrayList<>();

    protected PronunciationMiniConversationEntity() {}

    static PronunciationMiniConversationEntity fromAggregate(PronunciationMiniConversation conversation) {
        PronunciationMiniConversationEntity entity = new PronunciationMiniConversationEntity();
        entity.id = conversation.id().value();
        entity.isNew = true;
        entity.userId = conversation.userId();
        entity.focus = conversation.focus();
        entity.level = conversation.level();
        entity.status = conversation.status().name();
        entity.currentPrompt = conversation.currentPrompt();
        entity.currentTargetPhrase = conversation.currentTargetPhrase();
        entity.startedAt = conversation.startedAt();
        entity.completedAt = conversation.completedAt();
        entity.turnEntities = conversation.turns().stream()
                .map(t -> MiniConversationTurnEntity.fromDomain(t, entity))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        return entity;
    }

    PronunciationMiniConversation toAggregate() {
        List<MiniConversationTurn> turns = turnEntities.stream()
                .map(MiniConversationTurnEntity::toDomain)
                .toList();
        return PronunciationMiniConversation.reconstitute(
                new PronunciationMiniConversationId(id), userId, focus, level,
                MiniConversationStatus.valueOf(status), currentPrompt, currentTargetPhrase,
                turns, startedAt, completedAt);
    }

    void updateFrom(PronunciationMiniConversation conversation) {
        this.status = conversation.status().name();
        this.currentPrompt = conversation.currentPrompt();
        this.currentTargetPhrase = conversation.currentTargetPhrase();
        this.completedAt = conversation.completedAt();

        List<UUID> existingIds = turnEntities.stream()
                .map(MiniConversationTurnEntity::getId)
                .toList();

        for (MiniConversationTurn turn : conversation.turns()) {
            if (!existingIds.contains(turn.id())) {
                turnEntities.add(MiniConversationTurnEntity.fromDomain(turn, this));
            }
        }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
