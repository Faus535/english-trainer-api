package com.faus535.englishtrainer.learningpath.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SpringDataLearningUnitRepository extends JpaRepository<LearningUnitEntity, UUID> {

    List<LearningUnitEntity> findByLearningPathId(UUID learningPathId);

    List<LearningUnitEntity> findByLearningPathIdOrderByUnitIndex(UUID learningPathId);
}
