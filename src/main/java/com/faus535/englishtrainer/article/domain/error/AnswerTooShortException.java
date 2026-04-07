package com.faus535.englishtrainer.article.domain.error;

public class AnswerTooShortException extends ArticleException {

    public AnswerTooShortException(int wordCount, int minWords) {
        super("Answer too short: " + wordCount + " words (minimum " + minWords + ")");
    }
}
