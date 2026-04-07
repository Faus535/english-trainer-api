package com.faus535.englishtrainer.article.domain;

import java.util.UUID;

public record ArticleQuestionId(UUID value) {

    public ArticleQuestionId {
        if (value == null) throw new IllegalArgumentException("ArticleQuestionId cannot be null");
    }

    public static ArticleQuestionId generate() {
        return new ArticleQuestionId(UUID.randomUUID());
    }
}
