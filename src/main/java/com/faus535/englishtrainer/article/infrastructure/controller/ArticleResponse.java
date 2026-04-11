package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.domain.ArticleParagraph;
import com.faus535.englishtrainer.article.domain.ArticleReading;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

record ArticleResponse(
        UUID id,
        String title,
        String topic,
        String level,
        String status,
        List<ParagraphResponse> paragraphs,
        int currentParagraphIndex,
        int currentQuestionIndex,
        Instant createdAt
) {
    record ParagraphResponse(UUID id, String content, int orderIndex, String speaker) {}

    static ArticleResponse from(ArticleReading reading) {
        List<ParagraphResponse> paragraphs = reading.paragraphs().stream()
                .map(p -> new ParagraphResponse(p.id().value(), p.content(), p.orderIndex(), p.speaker().name()))
                .toList();
        return new ArticleResponse(
                reading.id().value(),
                reading.title(),
                reading.topic().value(),
                reading.level().value(),
                reading.status().value(),
                paragraphs,
                reading.currentParagraphIndex(),
                reading.currentQuestionIndex(),
                reading.createdAt());
    }
}
