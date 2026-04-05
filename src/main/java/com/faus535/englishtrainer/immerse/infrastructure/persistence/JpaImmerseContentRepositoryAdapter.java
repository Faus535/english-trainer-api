package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentId;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaImmerseContentRepositoryAdapter implements ImmerseContentRepository {

    private final JpaImmerseContentRepository jpaRepository;

    JpaImmerseContentRepositoryAdapter(JpaImmerseContentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ImmerseContent save(ImmerseContent content) {
        Optional<ImmerseContentEntity> existing = jpaRepository.findById(content.id().value());
        if (existing.isPresent()) {
            ImmerseContentEntity entity = existing.get();
            entity.updateFrom(content);
            return jpaRepository.save(entity).toAggregate();
        }
        return jpaRepository.save(ImmerseContentEntity.fromAggregate(content)).toAggregate();
    }

    @Override
    public Optional<ImmerseContent> findById(ImmerseContentId id) {
        return jpaRepository.findById(id.value()).map(ImmerseContentEntity::toAggregate);
    }

    @Override
    public List<ImmerseContent> findByUserId(UUID userId, int page, int size) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(ImmerseContentEntity::toAggregate)
                .toList();
    }
}
