package com.faus535.englishtrainer.article.domain.error;

import com.faus535.englishtrainer.article.domain.ArticleQuestionId;

public class ArticleQuestionNotFoundException extends ArticleException {

    public ArticleQuestionNotFoundException(ArticleQuestionId id) {
        super("Article question not found: " + id.value());
    }
}
