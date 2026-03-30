package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.PhonemeDailyAssignment;
import com.faus535.englishtrainer.phonetics.domain.PhonemeDailyAssignmentRepository;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
class JpaPhonemeDailyAssignmentRepositoryAdapter implements PhonemeDailyAssignmentRepository {

    private final JpaPhonemeDailyAssignmentRepository jpaRepository;

    JpaPhonemeDailyAssignmentRepositoryAdapter(JpaPhonemeDailyAssignmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<PhonemeDailyAssignment> findByUserAndDate(UserProfileId userId, LocalDate date) {
        return jpaRepository.findByUserIdAndAssignedDate(userId.value(), date)
                .map(PhonemeDailyAssignmentEntity::toAggregate);
    }

    @Override
    public Optional<PhonemeDailyAssignment> findByUserAndPhoneme(UserProfileId userId, PhonemeId phonemeId) {
        return jpaRepository.findByUserIdAndPhonemeId(userId.value(), phonemeId.value())
                .map(PhonemeDailyAssignmentEntity::toAggregate);
    }

    @Override
    public PhonemeDailyAssignment save(PhonemeDailyAssignment assignment) {
        PhonemeDailyAssignmentEntity entity = PhonemeDailyAssignmentEntity.fromAggregate(assignment);
        if (jpaRepository.existsById(assignment.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}
