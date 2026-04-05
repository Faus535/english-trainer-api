package com.faus535.englishtrainer.talk.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.talk.domain.TalkCorrection;
import com.faus535.englishtrainer.talk.domain.TalkMessage;
import com.faus535.englishtrainer.talk.domain.TalkMessageId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "talk_messages")
class TalkMessageEntity {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private TalkConversationEntity conversation;

    @Column(nullable = false, length = 10)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "correction_json", columnDefinition = "TEXT")
    private String correctionJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TalkMessageEntity() {}

    static TalkMessageEntity fromMessage(TalkMessage message, TalkConversationEntity conversation) {
        TalkMessageEntity entity = new TalkMessageEntity();
        entity.id = message.id().value();
        entity.conversation = conversation;
        entity.role = message.role();
        entity.content = message.content();
        entity.createdAt = message.createdAt();
        if (message.correction() != null) {
            try {
                entity.correctionJson = MAPPER.writeValueAsString(message.correction());
            } catch (JsonProcessingException e) {
                entity.correctionJson = null;
            }
        }
        return entity;
    }

    TalkMessage toMessage() {
        TalkCorrection correction = null;
        if (correctionJson != null) {
            try {
                correction = MAPPER.readValue(correctionJson, TalkCorrection.class);
            } catch (JsonProcessingException ignored) {}
        }
        return TalkMessage.reconstitute(
                new TalkMessageId(id), role, content, correction, createdAt);
    }

    UUID getId() { return id; }
}
