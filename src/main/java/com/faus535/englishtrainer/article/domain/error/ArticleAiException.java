package com.faus535.englishtrainer.article.domain.error;

public class ArticleAiException extends ArticleException {

    public ArticleAiException(String message) {
        super(message);
    }

    public ArticleAiException(String message, Throwable cause) {
        super(message, cause);
    }
}
