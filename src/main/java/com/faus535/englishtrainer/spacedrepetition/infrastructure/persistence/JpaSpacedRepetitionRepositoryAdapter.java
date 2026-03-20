package com.faus535.englishtrainer.spacedrepetition.infrastructure.persistence;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
class JpaSpacedRepetitionRepositoryAdapter implements SpacedRepetitionRepository {

    private final JpaSpacedRepetitionRepository jpaRepository;

    JpaSpacedRepetitionRepositoryAdapter(JpaSpacedRepetitionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<SpacedRepetitionItem> findById(SpacedRepetitionItemId id) {
        return jpaRepository.findById(id.value())
                .map(SpacedRepetitionItemEntity::toAggregate);
    }

    @Override
    public Optional<SpacedRepetitionItem> findByUserAndUnitReference(UserProfileId userId, String unitReference) {
        return jpaRepository.findByUserIdAndUnitReference(userId.value(), unitReference)
                .map(SpacedRepetitionItemEntity::toAggregate);
    }

    @Override
    public List<SpacedRepetitionItem> findDueByUser(UserProfileId userId, LocalDate today) {
        return jpaRepository.findByUserIdAndNextReviewDateLessThanEqualAndGraduatedFalse(userId.value(), today)
                .stream()
                .map(SpacedRepetitionItemEntity::toAggregate)
                .toList();
    }

    @Override
    public List<SpacedRepetitionItem> findAllByUser(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(SpacedRepetitionItemEntity::toAggregate)
                .toList();
    }

    @Override
    public SpacedRepetitionItem save(SpacedRepetitionItem item) {
        SpacedRepetitionItemEntity entity = SpacedRepetitionItemEntity.fromAggregate(item);
        if (jpaRepository.existsById(item.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}
