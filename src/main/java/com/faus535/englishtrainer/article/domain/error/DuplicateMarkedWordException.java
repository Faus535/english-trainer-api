package com.faus535.englishtrainer.article.domain.error;

public class DuplicateMarkedWordException extends ArticleException {

    public DuplicateMarkedWordException(String wordOrPhrase) {
        super("Word or phrase already marked: " + wordOrPhrase);
    }
}
