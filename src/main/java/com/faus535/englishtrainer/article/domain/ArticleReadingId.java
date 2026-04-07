package com.faus535.englishtrainer.article.domain;

import java.util.UUID;

public record ArticleReadingId(UUID value) {

    public ArticleReadingId {
        if (value == null) throw new IllegalArgumentException("ArticleReadingId cannot be null");
    }

    public static ArticleReadingId generate() {
        return new ArticleReadingId(UUID.randomUUID());
    }
}
