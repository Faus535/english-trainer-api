package com.faus535.englishtrainer.article.domain.error;

public class ArticleException extends Exception {

    public ArticleException(String message) {
        super(message);
    }

    public ArticleException(String message, Throwable cause) {
        super(message, cause);
    }
}
