package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleQuestionAnswer;
import com.faus535.englishtrainer.article.domain.ArticleQuestionAnswerRepository;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import com.faus535.englishtrainer.article.domain.error.QuestionAlreadyAnsweredException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaArticleQuestionAnswerRepositoryAdapter implements ArticleQuestionAnswerRepository {

    private final JpaArticleQuestionAnswerRepository jpaRepository;

    JpaArticleQuestionAnswerRepositoryAdapter(JpaArticleQuestionAnswerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ArticleQuestionAnswer answer) throws QuestionAlreadyAnsweredException {
        try {
            jpaRepository.saveAndFlush(ArticleQuestionAnswerEntity.fromDomain(answer));
        } catch (DataIntegrityViolationException e) {
            throw new QuestionAlreadyAnsweredException(answer.questionId());
        }
    }

    @Override
    public Optional<ArticleQuestionAnswer> findByQuestionId(ArticleQuestionId questionId) {
        return jpaRepository.findByArticleQuestionId(questionId.value())
                .map(ArticleQuestionAnswerEntity::toDomain);
    }
}
