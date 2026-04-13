package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.MiniConversationTurn;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pronunciation_mini_conversation_turns")
class MiniConversationTurnEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private PronunciationMiniConversationEntity conversation;

    @Column(name = "turn_number", nullable = false)
    private int turnNumber;

    @Column(name = "target_phrase", nullable = false, columnDefinition = "TEXT")
    private String targetPhrase;

    @Column(name = "recognized_text", columnDefinition = "TEXT")
    private String recognizedText;

    @Column(nullable = false)
    private int score;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt;

    protected MiniConversationTurnEntity() {}

    static MiniConversationTurnEntity fromDomain(MiniConversationTurn turn,
            PronunciationMiniConversationEntity conversation) {
        MiniConversationTurnEntity entity = new MiniConversationTurnEntity();
        entity.id = turn.id();
        entity.isNew = true;
        entity.conversation = conversation;
        entity.turnNumber = turn.turnNumber();
        entity.targetPhrase = turn.targetPhrase();
        entity.recognizedText = turn.recognizedText();
        entity.score = turn.score();
        entity.evaluatedAt = turn.evaluatedAt();
        return entity;
    }

    MiniConversationTurn toDomain() {
        return new MiniConversationTurn(id, turnNumber, targetPhrase, recognizedText, score, evaluatedAt);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
