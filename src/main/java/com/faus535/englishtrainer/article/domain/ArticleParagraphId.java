package com.faus535.englishtrainer.article.domain;

import java.util.UUID;

public record ArticleParagraphId(UUID value) {

    public ArticleParagraphId {
        if (value == null) throw new IllegalArgumentException("ArticleParagraphId cannot be null");
    }

    public static ArticleParagraphId generate() {
        return new ArticleParagraphId(UUID.randomUUID());
    }
}
