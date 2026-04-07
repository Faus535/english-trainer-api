package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleMarkedWord;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class JpaArticleMarkedWordRepositoryAdapter implements ArticleMarkedWordRepository {

    private final JpaArticleMarkedWordRepository jpaRepository;

    JpaArticleMarkedWordRepositoryAdapter(JpaArticleMarkedWordRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ArticleMarkedWord word) throws DuplicateMarkedWordException {
        try {
            jpaRepository.saveAndFlush(ArticleMarkedWordEntity.fromDomain(word));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateMarkedWordException(word.wordOrPhrase());
        }
    }

    @Override
    public List<ArticleMarkedWord> findByArticleIdAndUserId(ArticleReadingId articleReadingId, UUID userId) {
        return jpaRepository.findByArticleReadingIdAndUserId(articleReadingId.value(), userId)
                .stream()
                .map(ArticleMarkedWordEntity::toDomain)
                .toList();
    }
}
