package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaArticleReadingRepositoryAdapter implements ArticleReadingRepository {

    private final JpaArticleReadingRepository jpaRepository;

    JpaArticleReadingRepositoryAdapter(JpaArticleReadingRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ArticleReading reading) {
        Optional<ArticleReadingEntity> existing = jpaRepository.findById(reading.id().value());
        if (existing.isPresent()) {
            ArticleReadingEntity entity = existing.get();
            entity.updateFrom(reading);
            jpaRepository.saveAndFlush(entity);
        } else {
            jpaRepository.saveAndFlush(ArticleReadingEntity.fromDomain(reading));
        }
    }

    @Override
    public Optional<ArticleReading> findById(ArticleReadingId id) {
        return jpaRepository.findById(id.value()).map(ArticleReadingEntity::toDomain);
    }

    @Override
    public List<ArticleReading> findByUserIdOrderByCreatedAtDesc(UUID userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ArticleReadingEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(ArticleReadingId id) {
        jpaRepository.deleteById(id.value());
        jpaRepository.flush();
    }
}
