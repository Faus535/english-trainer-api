package com.faus535.englishtrainer.article.domain.error;

import com.faus535.englishtrainer.article.domain.ArticleReadingId;

public class ArticleNotFoundException extends ArticleException {

    public ArticleNotFoundException(ArticleReadingId id) {
        super("Article not found: " + id.value());
    }
}
