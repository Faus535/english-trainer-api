package com.faus535.englishtrainer.minimalpair.infrastructure.persistence;

import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResult;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResultRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class JpaMinimalPairResultRepositoryAdapter implements MinimalPairResultRepository {

    private final JpaMinimalPairResultRepository jpaRepository;

    JpaMinimalPairResultRepositoryAdapter(JpaMinimalPairResultRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MinimalPairResult save(MinimalPairResult result) {
        MinimalPairResultEntity entity = MinimalPairResultEntity.fromAggregate(result);
        if (jpaRepository.existsById(result.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }

    @Override
    public List<MinimalPairResult> findByUserId(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(MinimalPairResultEntity::toAggregate)
                .toList();
    }

    @Override
    public List<CategoryAccuracy> countByUserIdAndCorrectGroupedByCategory(UserProfileId userId) {
        return jpaRepository.findAccuracyByUserId(userId.value()).stream()
                .map(p -> new CategoryAccuracy(p.getSoundCategory(), p.getTotal(), p.getCorrect()))
                .toList();
    }
}
