package com.faus535.englishtrainer.article.domain.error;

import com.faus535.englishtrainer.article.domain.ArticleReadingId;

public class ArticleCannotBeDeletedException extends ArticleException {

    public ArticleCannotBeDeletedException(ArticleReadingId id) {
        super("Cannot delete completed article: " + id.value());
    }
}
