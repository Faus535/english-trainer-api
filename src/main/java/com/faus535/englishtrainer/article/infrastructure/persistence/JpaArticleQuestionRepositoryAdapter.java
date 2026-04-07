package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleQuestion;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import com.faus535.englishtrainer.article.domain.ArticleQuestionRepository;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaArticleQuestionRepositoryAdapter implements ArticleQuestionRepository {

    private final JpaArticleQuestionRepository jpaRepository;

    JpaArticleQuestionRepositoryAdapter(JpaArticleQuestionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ArticleQuestion question) {
        jpaRepository.saveAndFlush(ArticleQuestionEntity.fromDomain(question));
    }

    @Override
    public List<ArticleQuestion> findByArticleReadingId(ArticleReadingId articleReadingId) {
        return jpaRepository.findByArticleReadingIdOrderByOrderIndexAsc(articleReadingId.value())
                .stream().map(ArticleQuestionEntity::toDomain).toList();
    }

    @Override
    public Optional<ArticleQuestion> findById(ArticleQuestionId id) {
        return jpaRepository.findById(id.value()).map(ArticleQuestionEntity::toDomain);
    }
}
