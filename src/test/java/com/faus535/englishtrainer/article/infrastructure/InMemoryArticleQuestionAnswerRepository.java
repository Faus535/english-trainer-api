package com.faus535.englishtrainer.article.infrastructure;

import com.faus535.englishtrainer.article.domain.ArticleQuestionAnswer;
import com.faus535.englishtrainer.article.domain.ArticleQuestionAnswerRepository;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import com.faus535.englishtrainer.article.domain.error.QuestionAlreadyAnsweredException;

import java.util.*;

public class InMemoryArticleQuestionAnswerRepository implements ArticleQuestionAnswerRepository {

    private final Map<UUID, ArticleQuestionAnswer> store = new LinkedHashMap<>();

    @Override
    public void save(ArticleQuestionAnswer answer) throws QuestionAlreadyAnsweredException {
        if (store.values().stream()
                .anyMatch(a -> a.questionId().equals(answer.questionId()))) {
            throw new QuestionAlreadyAnsweredException(answer.questionId());
        }
        store.put(answer.id().value(), answer);
    }

    @Override
    public Optional<ArticleQuestionAnswer> findByQuestionId(ArticleQuestionId questionId) {
        return store.values().stream()
                .filter(a -> a.questionId().equals(questionId))
                .findFirst();
    }

    public int count() { return store.size(); }
}
