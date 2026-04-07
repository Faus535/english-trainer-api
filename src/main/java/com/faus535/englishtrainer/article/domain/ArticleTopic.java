package com.faus535.englishtrainer.article.domain;

public record ArticleTopic(String value) {

    public ArticleTopic {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("ArticleTopic cannot be blank");
        if (value.length() > 100) throw new IllegalArgumentException("ArticleTopic cannot exceed 100 characters");
    }
}
