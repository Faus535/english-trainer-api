package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.VocabularyItem;

import java.time.Instant;
import java.util.List;

record ImmerseContentResponse(
        String id, String title, String sourceUrl, String processedText,
        String cefrLevel, List<VocabularyItem> extractedVocabulary,
        String status, Instant createdAt
) {
    static ImmerseContentResponse from(ImmerseContent content) {
        return new ImmerseContentResponse(
                content.id().value().toString(), content.title(), content.sourceUrl(),
                content.processedText(), content.cefrLevel(), content.extractedVocabulary(),
                content.status().value(), content.createdAt());
    }
}
