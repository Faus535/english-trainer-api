package com.faus535.englishtrainer.talk.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.ConversationMode;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "talk_conversations")
class TalkConversationEntity implements Persistable<UUID> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "scenario_id")
    private UUID scenarioId;

    @Column(nullable = false, length = 5)
    private String level;

    @Column(nullable = false, length = 10)
    private String mode;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "evaluation_json", columnDefinition = "TEXT")
    private String evaluationJson;

    @Column(name = "grammar_notes", columnDefinition = "TEXT")
    private String grammarNotesJson;

    @Column(name = "vocabulary_used", columnDefinition = "TEXT")
    private String vocabularyUsedJson;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<TalkMessageEntity> messageEntities = new ArrayList<>();

    protected TalkConversationEntity() {}

    static TalkConversationEntity fromAggregate(TalkConversation aggregate) {
        TalkConversationEntity entity = new TalkConversationEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId();
        entity.scenarioId = aggregate.scenarioId();
        entity.level = aggregate.level().value();
        entity.mode = aggregate.mode().name();
        entity.status = aggregate.status().value();
        entity.summary = aggregate.summary();
        entity.evaluationJson = serializeEvaluation(aggregate.evaluation());
        entity.grammarNotesJson = serializeList(aggregate.grammarNotes(), GrammarNote.class);
        entity.vocabularyUsedJson = serializeList(aggregate.newVocabulary(), VocabItem.class);
        entity.startedAt = aggregate.startedAt();
        entity.endedAt = aggregate.endedAt();
        entity.messageEntities = aggregate.messages().stream()
                .map(msg -> TalkMessageEntity.fromMessage(msg, entity))
                .toList();
        return entity;
    }

    TalkConversation toAggregate() {
        List<TalkMessage> messages = messageEntities.stream()
                .map(TalkMessageEntity::toMessage)
                .toList();
        return TalkConversation.reconstitute(
                new TalkConversationId(id), userId, scenarioId,
                new TalkLevel(level), ConversationMode.fromString(mode), TalkStatus.fromString(status), summary,
                deserializeEvaluation(evaluationJson),
                deserializeList(grammarNotesJson, GrammarNote.class),
                deserializeList(vocabularyUsedJson, VocabItem.class),
                startedAt, endedAt, messages);
    }

    void updateFrom(TalkConversation aggregate) {
        this.status = aggregate.status().value();
        this.summary = aggregate.summary();
        this.endedAt = aggregate.endedAt();
        this.evaluationJson = serializeEvaluation(aggregate.evaluation());
        this.grammarNotesJson = serializeList(aggregate.grammarNotes(), GrammarNote.class);
        this.vocabularyUsedJson = serializeList(aggregate.newVocabulary(), VocabItem.class);

        List<UUID> existingIds = messageEntities.stream()
                .map(TalkMessageEntity::getId)
                .toList();

        for (TalkMessage msg : aggregate.messages()) {
            if (!existingIds.contains(msg.id().value())) {
                messageEntities.add(TalkMessageEntity.fromMessage(msg, this));
            }
        }
    }

    private static <T> String serializeList(List<T> list, Class<T> type) {
        if (list == null) return null;
        try {
            return MAPPER.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }

    private static <T> List<T> deserializeList(String json, Class<T> type) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, type));
        } catch (Exception e) {
            return null;
        }
    }

    private static String serializeEvaluation(TalkEvaluation evaluation) {
        if (evaluation == null) return null;
        try {
            return MAPPER.writeValueAsString(evaluation);
        } catch (Exception e) {
            return null;
        }
    }

    private static TalkEvaluation deserializeEvaluation(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, TalkEvaluation.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
