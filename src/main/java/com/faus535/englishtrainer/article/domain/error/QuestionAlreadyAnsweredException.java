package com.faus535.englishtrainer.article.domain.error;

import com.faus535.englishtrainer.article.domain.ArticleQuestionId;

public class QuestionAlreadyAnsweredException extends ArticleException {

    public QuestionAlreadyAnsweredException(ArticleQuestionId id) {
        super("Question already answered: " + id.value());
    }
}
