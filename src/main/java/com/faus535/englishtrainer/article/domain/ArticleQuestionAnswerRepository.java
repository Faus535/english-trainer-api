package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.QuestionAlreadyAnsweredException;

import java.util.Optional;

public interface ArticleQuestionAnswerRepository {

    void save(ArticleQuestionAnswer answer) throws QuestionAlreadyAnsweredException;

    Optional<ArticleQuestionAnswer> findByQuestionId(ArticleQuestionId questionId);
}
