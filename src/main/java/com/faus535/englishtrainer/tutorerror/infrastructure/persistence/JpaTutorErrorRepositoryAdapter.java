package com.faus535.englishtrainer.tutorerror.infrastructure.persistence;

import com.faus535.englishtrainer.tutorerror.domain.TutorError;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorId;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
class JpaTutorErrorRepositoryAdapter implements TutorErrorRepository {

    private final JpaTutorErrorRepository jpaRepository;

    JpaTutorErrorRepositoryAdapter(JpaTutorErrorRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<TutorError> findById(TutorErrorId id) {
        return jpaRepository.findById(id.value())
                .map(TutorErrorEntity::toAggregate);
    }

    @Override
    public List<TutorError> findByUserId(UserProfileId userId) {
        return jpaRepository.findByUserIdOrderByOccurrenceCountDesc(userId.value()).stream()
                .map(TutorErrorEntity::toAggregate)
                .toList();
    }

    @Override
    public List<TutorError> findByUserIdAndType(UserProfileId userId, String errorType) {
        return jpaRepository.findByUserIdAndErrorTypeOrderByOccurrenceCountDesc(userId.value(), errorType).stream()
                .map(TutorErrorEntity::toAggregate)
                .toList();
    }

    @Override
    public Optional<TutorError> findByUserIdAndOriginalAndCorrected(UserProfileId userId,
                                                                      String originalText,
                                                                      String correctedText) {
        return jpaRepository.findByUserIdAndOriginalTextAndCorrectedText(
                        userId.value(), originalText, correctedText)
                .map(TutorErrorEntity::toAggregate);
    }

    @Override
    public TutorError save(TutorError tutorError) {
        Optional<TutorErrorEntity> existing = jpaRepository.findById(tutorError.id().value());
        if (existing.isPresent()) {
            TutorErrorEntity entity = existing.get();
            entity.updateFrom(tutorError);
            return jpaRepository.save(entity).toAggregate();
        }
        return jpaRepository.save(TutorErrorEntity.fromAggregate(tutorError)).toAggregate();
    }

    @Override
    public List<Map<String, Object>> countByUserIdGroupedByWeek(UserProfileId userId, int weeks) {
        return jpaRepository.countByUserIdGroupedByWeek(userId.value(), weeks);
    }
}
