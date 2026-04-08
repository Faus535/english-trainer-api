package com.faus535.englishtrainer.article.domain;

import java.time.Instant;
import java.util.UUID;

public record ArticleReadingSummary(
        UUID articleReadingId,
        String title,
        String level,
        String topic,
        String status,
        Instant createdAt,
        int wordCount,
        int questionsAnswered
) {}
