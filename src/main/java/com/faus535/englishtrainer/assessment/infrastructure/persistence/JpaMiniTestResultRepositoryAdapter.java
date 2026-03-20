package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import com.faus535.englishtrainer.assessment.domain.MiniTestResult;
import com.faus535.englishtrainer.assessment.domain.MiniTestResultRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
class JpaMiniTestResultRepositoryAdapter implements MiniTestResultRepository {

    private final JpaMiniTestResultRepository jpaRepository;

    JpaMiniTestResultRepositoryAdapter(JpaMiniTestResultRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<MiniTestResult> findByUser(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(MiniTestResultEntity::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<MiniTestResult> findByUserAndModule(UserProfileId userId, String moduleName) {
        return jpaRepository.findByUserIdAndModuleName(userId.value(), moduleName)
                .stream()
                .map(MiniTestResultEntity::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public MiniTestResult save(MiniTestResult result) {
        MiniTestResultEntity entity = MiniTestResultEntity.fromAggregate(result);
        if (jpaRepository.existsById(result.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}
