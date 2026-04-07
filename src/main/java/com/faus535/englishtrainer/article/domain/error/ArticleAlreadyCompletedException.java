package com.faus535.englishtrainer.article.domain.error;

import com.faus535.englishtrainer.article.domain.ArticleReadingId;

public class ArticleAlreadyCompletedException extends ArticleException {

    public ArticleAlreadyCompletedException(ArticleReadingId id) {
        super("Article already completed: " + id.value());
    }
}
