package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationError;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationErrorRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaPronunciationErrorRepositoryAdapter implements PronunciationErrorRepository {

    private final JpaPronunciationErrorRepository jpaRepository;

    JpaPronunciationErrorRepositoryAdapter(JpaPronunciationErrorRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PronunciationError save(PronunciationError error) {
        Optional<PronunciationErrorEntity> existing = jpaRepository.findById(error.id().value());
        if (existing.isPresent()) {
            PronunciationErrorEntity entity = existing.get();
            entity.updateFrom(error);
            return jpaRepository.save(entity).toAggregate();
        }
        return jpaRepository.save(PronunciationErrorEntity.fromAggregate(error)).toAggregate();
    }

    @Override
    public List<PronunciationError> findByUserId(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(PronunciationErrorEntity::toAggregate)
                .toList();
    }

    @Override
    public Optional<PronunciationError> findByUserIdAndWordAndPhoneme(UserProfileId userId, String word, String expectedPhoneme) {
        return jpaRepository.findByUserIdAndWordAndExpectedPhoneme(userId.value(), word, expectedPhoneme)
                .map(PronunciationErrorEntity::toAggregate);
    }
}
