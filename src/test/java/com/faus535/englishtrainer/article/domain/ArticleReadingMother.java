package com.faus535.englishtrainer.article.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ArticleReadingMother {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static ArticleReading inProgress(UUID userId) {
        ArticleReadingId id = ArticleReadingId.generate();
        List<ArticleParagraph> paragraphs = List.of(
                ArticleParagraph.create(id, "European leaders gathered in Brussels...", 0, ArticleSpeaker.AI),
                ArticleParagraph.create(id, "Critics argue that the timeline...", 1, ArticleSpeaker.USER)
        );
        return ArticleReading.reconstitute(id, userId, new ArticleTopic("Climate change"),
                ArticleLevel.B2, "EU Climate Debate", ArticleStatus.IN_PROGRESS, paragraphs, Instant.now());
    }

    public static ArticleReading inProgress() {
        return inProgress(DEFAULT_USER_ID);
    }

    public static ArticleReading completed(UUID userId) {
        ArticleReadingId id = ArticleReadingId.generate();
        List<ArticleParagraph> paragraphs = List.of(
                ArticleParagraph.create(id, "European leaders gathered in Brussels...", 0, ArticleSpeaker.AI),
                ArticleParagraph.create(id, "Critics argue that the timeline...", 1, ArticleSpeaker.USER)
        );
        return ArticleReading.reconstitute(id, userId, new ArticleTopic("Climate change"),
                ArticleLevel.B2, "EU Climate Debate", ArticleStatus.COMPLETED, paragraphs, Instant.now());
    }

    public static ArticleReading completed() {
        return completed(DEFAULT_USER_ID);
    }

    public static ArticleReading withId(ArticleReadingId id, UUID userId) {
        List<ArticleParagraph> paragraphs = List.of(
                ArticleParagraph.create(id, "Test paragraph AI...", 0, ArticleSpeaker.AI),
                ArticleParagraph.create(id, "Test paragraph USER...", 1, ArticleSpeaker.USER)
        );
        return ArticleReading.reconstitute(id, userId, new ArticleTopic("Technology"),
                ArticleLevel.B1, "Tech Article", ArticleStatus.IN_PROGRESS, paragraphs, Instant.now());
    }
}
