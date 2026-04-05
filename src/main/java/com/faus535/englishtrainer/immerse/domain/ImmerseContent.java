package com.faus535.englishtrainer.immerse.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ImmerseContent extends AggregateRoot<ImmerseContentId> {

    private final ImmerseContentId id;
    private final UUID userId;
    private final String sourceUrl;
    private final String title;
    private final String rawText;
    private final String processedText;
    private final String cefrLevel;
    private final List<VocabularyItem> extractedVocabulary;
    private final ContentType contentType;
    private final ImmerseContentStatus status;
    private final Instant createdAt;

    private ImmerseContent(ImmerseContentId id, UUID userId, String sourceUrl, String title,
                           String rawText, String processedText, String cefrLevel,
                           List<VocabularyItem> extractedVocabulary, ContentType contentType,
                           ImmerseContentStatus status, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.rawText = rawText;
        this.processedText = processedText;
        this.cefrLevel = cefrLevel;
        this.extractedVocabulary = extractedVocabulary != null ? List.copyOf(extractedVocabulary) : List.of();
        this.contentType = contentType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static ImmerseContent submit(UUID userId, String sourceUrl, String title, String rawText) {
        return new ImmerseContent(
                ImmerseContentId.generate(), userId, sourceUrl, title, rawText,
                null, null, List.of(), ContentType.TEXT, ImmerseContentStatus.PENDING, Instant.now());
    }

    public static ImmerseContent generate(UUID userId, ContentType contentType, String title,
                                           String rawText, String processedText, String cefrLevel,
                                           List<VocabularyItem> vocabulary) {
        return new ImmerseContent(
                ImmerseContentId.generate(), userId, null, title, rawText,
                processedText, cefrLevel, vocabulary, contentType,
                ImmerseContentStatus.PROCESSED, Instant.now());
    }

    public static ImmerseContent reconstitute(ImmerseContentId id, UUID userId, String sourceUrl,
                                               String title, String rawText, String processedText,
                                               String cefrLevel, List<VocabularyItem> vocabulary,
                                               ContentType contentType, ImmerseContentStatus status,
                                               Instant createdAt) {
        return new ImmerseContent(id, userId, sourceUrl, title, rawText, processedText,
                cefrLevel, vocabulary, contentType, status, createdAt);
    }

    public ImmerseContent markProcessed(String processedText, String detectedLevel,
                                         List<VocabularyItem> vocabulary) {
        return new ImmerseContent(id, userId, sourceUrl, title, rawText, processedText,
                detectedLevel, vocabulary, contentType, ImmerseContentStatus.PROCESSED, createdAt);
    }

    public ImmerseContent markFailed() {
        return new ImmerseContent(id, userId, sourceUrl, title, rawText, processedText,
                cefrLevel, extractedVocabulary, contentType, ImmerseContentStatus.FAILED, createdAt);
    }

    public ImmerseContentId id() { return id; }
    public UUID userId() { return userId; }
    public String sourceUrl() { return sourceUrl; }
    public String title() { return title; }
    public String rawText() { return rawText; }
    public String processedText() { return processedText; }
    public String cefrLevel() { return cefrLevel; }
    public List<VocabularyItem> extractedVocabulary() { return extractedVocabulary; }
    public ContentType contentType() { return contentType; }
    public ImmerseContentStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
}
