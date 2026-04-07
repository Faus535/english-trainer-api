package com.faus535.englishtrainer.article.domain;

import java.util.UUID;

public record ArticleMarkedWordId(UUID value) {

    public ArticleMarkedWordId {
        if (value == null) throw new IllegalArgumentException("ArticleMarkedWordId cannot be null");
    }

    public static ArticleMarkedWordId generate() {
        return new ArticleMarkedWordId(UUID.randomUUID());
    }
}
