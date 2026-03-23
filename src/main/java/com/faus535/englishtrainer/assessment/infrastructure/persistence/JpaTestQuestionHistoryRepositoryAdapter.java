package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import com.faus535.englishtrainer.assessment.domain.TestQuestionHistoryRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class JpaTestQuestionHistoryRepositoryAdapter implements TestQuestionHistoryRepository {

    private final JpaTestQuestionHistoryRepository jpaRepository;

    JpaTestQuestionHistoryRepositoryAdapter(JpaTestQuestionHistoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<UUID> findQuestionIdsByUser(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(TestQuestionHistoryEntity::questionId)
                .toList();
    }

    @Override
    public void saveAll(UserProfileId userId, List<UUID> questionIds) {
        List<TestQuestionHistoryEntity> entities = questionIds.stream()
                .map(qId -> TestQuestionHistoryEntity.create(userId.value(), qId))
                .toList();
        jpaRepository.saveAll(entities);
    }
}
