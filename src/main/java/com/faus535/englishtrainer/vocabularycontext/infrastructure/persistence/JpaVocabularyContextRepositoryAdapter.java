package com.faus535.englishtrainer.vocabularycontext.infrastructure.persistence;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabularycontext.domain.VocabularyContext;
import com.faus535.englishtrainer.vocabularycontext.domain.VocabularyContextRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaVocabularyContextRepositoryAdapter implements VocabularyContextRepository {

    private final JpaVocabularyContextRepository jpaRepository;

    JpaVocabularyContextRepositoryAdapter(JpaVocabularyContextRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<VocabularyContext> findByVocabularyIdAndLevel(VocabEntryId vocabularyId, String level) {
        return jpaRepository.findByVocabularyIdAndLevel(vocabularyId.value(), level)
                .map(VocabularyContextEntity::toAggregate);
    }

    @Override
    public VocabularyContext save(VocabularyContext context) {
        VocabularyContextEntity entity = VocabularyContextEntity.fromAggregate(context);
        if (jpaRepository.existsById(context.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}
