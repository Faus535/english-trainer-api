package com.faus535.englishtrainer.article.domain;

import java.util.UUID;

public record ArticleQuestionAnswerId(UUID value) {

    public ArticleQuestionAnswerId {
        if (value == null) throw new IllegalArgumentException("ArticleQuestionAnswerId cannot be null");
    }

    public static ArticleQuestionAnswerId generate() {
        return new ArticleQuestionAnswerId(UUID.randomUUID());
    }
}
