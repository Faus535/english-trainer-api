package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.immerse.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "immerse_content")
class ImmerseContentEntity implements Persistable<UUID> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "processed_text", columnDefinition = "TEXT")
    private String processedText;

    @Column(name = "cefr_level", length = 5)
    private String cefrLevel;

    @Column(name = "extracted_vocabulary", columnDefinition = "TEXT")
    private String extractedVocabularyJson;

    @Column(name = "content_type", length = 10)
    private String contentType;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ImmerseContentEntity() {}

    static ImmerseContentEntity fromAggregate(ImmerseContent aggregate) {
        ImmerseContentEntity entity = new ImmerseContentEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId();
        entity.sourceUrl = aggregate.sourceUrl();
        entity.title = aggregate.title();
        entity.rawText = aggregate.rawText();
        entity.processedText = aggregate.processedText();
        entity.cefrLevel = aggregate.cefrLevel();
        entity.extractedVocabularyJson = serializeVocabulary(aggregate.extractedVocabulary());
        entity.contentType = aggregate.contentType() != null ? aggregate.contentType().value() : null;
        entity.status = aggregate.status().value();
        entity.createdAt = aggregate.createdAt();
        return entity;
    }

    ImmerseContent toAggregate() {
        return ImmerseContent.reconstitute(
                new ImmerseContentId(id), userId, sourceUrl, title, rawText,
                processedText, cefrLevel, deserializeVocabulary(extractedVocabularyJson),
                contentType != null ? ContentType.fromString(contentType) : null,
                ImmerseContentStatus.fromString(status), createdAt);
    }

    void updateFrom(ImmerseContent aggregate) {
        this.title = aggregate.title();
        this.rawText = aggregate.rawText();
        this.processedText = aggregate.processedText();
        this.cefrLevel = aggregate.cefrLevel();
        this.extractedVocabularyJson = serializeVocabulary(aggregate.extractedVocabulary());
        this.contentType = aggregate.contentType() != null ? aggregate.contentType().value() : null;
        this.status = aggregate.status().value();
    }

    private static String serializeVocabulary(List<VocabularyItem> vocabulary) {
        if (vocabulary == null || vocabulary.isEmpty()) return null;
        try { return MAPPER.writeValueAsString(vocabulary); } catch (Exception e) { return null; }
    }

    private static List<VocabularyItem> deserializeVocabulary(String json) {
        if (json == null || json.isBlank()) return List.of();
        try { return MAPPER.readValue(json, new TypeReference<>() {}); } catch (Exception e) { return List.of(); }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
