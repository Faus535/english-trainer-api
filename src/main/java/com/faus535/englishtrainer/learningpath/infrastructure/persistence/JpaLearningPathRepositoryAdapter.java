package com.faus535.englishtrainer.learningpath.infrastructure.persistence;

import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaLearningPathRepositoryAdapter implements LearningPathRepository {

    private final SpringDataLearningPathRepository jpaRepository;
    private final SpringDataLearningUnitRepository unitJpaRepository;

    JpaLearningPathRepositoryAdapter(SpringDataLearningPathRepository jpaRepository,
                                      SpringDataLearningUnitRepository unitJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.unitJpaRepository = unitJpaRepository;
    }

    @Override
    public Optional<LearningPath> findById(LearningPathId id) {
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<LearningPath> findByUserId(UserProfileId userId) {
        return jpaRepository.findByUserProfileId(userId.value())
                .map(this::toDomain);
    }

    @Override
    public LearningPath save(LearningPath learningPath) {
        LearningPathEntity entity = LearningPathEntity.fromAggregate(
                learningPath.id().value(),
                learningPath.userId().value(),
                learningPath.currentLevel(),
                learningPath.currentUnitIndex(),
                learningPath.createdAt(),
                learningPath.updatedAt()
        );
        if (jpaRepository.existsById(learningPath.id().value())) {
            entity.markAsExisting();
        }
        jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public void deleteByUserId(UserProfileId userId) {
        jpaRepository.deleteByUserProfileId(userId.value());
    }

    private LearningPath toDomain(LearningPathEntity entity) {
        List<LearningUnitId> unitIds = unitJpaRepository
                .findByLearningPathIdOrderByUnitIndex(entity.getId())
                .stream()
                .map(u -> new LearningUnitId(u.getId()))
                .toList();

        return LearningPath.reconstitute(
                new LearningPathId(entity.getId()),
                new UserProfileId(entity.userProfileId()),
                entity.currentLevel(),
                entity.currentUnitIndex(),
                unitIds,
                entity.createdAt(),
                entity.updatedAt()
        );
    }
}
