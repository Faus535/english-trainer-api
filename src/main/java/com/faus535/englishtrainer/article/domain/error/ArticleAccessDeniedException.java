package com.faus535.englishtrainer.article.domain.error;

import com.faus535.englishtrainer.article.domain.ArticleReadingId;

public class ArticleAccessDeniedException extends ArticleException {

    public ArticleAccessDeniedException(ArticleReadingId id) {
        super("Access denied to article: " + id.value());
    }
}
