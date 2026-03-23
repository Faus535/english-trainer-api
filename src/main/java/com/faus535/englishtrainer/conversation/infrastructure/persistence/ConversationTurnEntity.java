package com.faus535.englishtrainer.conversation.infrastructure.persistence;

import com.faus535.englishtrainer.conversation.domain.ConversationTurn;
import com.faus535.englishtrainer.conversation.domain.ConversationTurnId;
import com.faus535.englishtrainer.conversation.domain.TutorFeedback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversation_turns")
class ConversationTurnEntity {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "feedback_json", columnDefinition = "JSONB")
    private String feedbackJson;

    @Column
    private Float confidence;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ConversationTurnEntity() {}

    static ConversationTurnEntity fromTurn(ConversationTurn turn, ConversationEntity conversation) {
        ConversationTurnEntity entity = new ConversationTurnEntity();
        entity.id = turn.id().value();
        entity.conversation = conversation;
        entity.role = turn.role();
        entity.content = turn.content();
        entity.confidence = turn.confidence();
        entity.createdAt = turn.createdAt();
        if (turn.feedback() != null) {
            try {
                entity.feedbackJson = MAPPER.writeValueAsString(turn.feedback());
            } catch (JsonProcessingException e) {
                entity.feedbackJson = null;
            }
        }
        return entity;
    }

    ConversationTurn toTurn() {
        TutorFeedback feedback = null;
        if (feedbackJson != null) {
            try {
                feedback = MAPPER.readValue(feedbackJson, TutorFeedback.class);
            } catch (JsonProcessingException ignored) {}
        }
        return ConversationTurn.reconstitute(
                new ConversationTurnId(id), role, content, feedback, confidence, createdAt);
    }

    UUID getId() { return id; }
}
