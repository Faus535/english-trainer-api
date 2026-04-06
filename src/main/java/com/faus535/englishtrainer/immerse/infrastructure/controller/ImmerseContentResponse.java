package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.VocabularyItem;

import java.time.Instant;
import java.util.List;

record ImmerseContentResponse(
        String id, String title, String sourceUrl, String processedText,
        String cefrLevel, List<VocabularyItem> extractedVocabulary,
        String contentType, String status, Instant createdAt
) {
    static ImmerseContentResponse from(ImmerseContent content) {
        String text = content.processedText() != null ? content.processedText() : content.rawText();
        return new ImmerseContentResponse(
                content.id().value().toString(), content.title(), content.sourceUrl(),
                text, content.cefrLevel(), content.extractedVocabulary(),
                content.contentType() != null ? content.contentType().value() : null,
                content.status().value(), content.createdAt());
    }
}
