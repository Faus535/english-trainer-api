package com.faus535.englishtrainer.learningpath.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataLearningPathRepository extends JpaRepository<LearningPathEntity, UUID> {

    Optional<LearningPathEntity> findByUserProfileId(UUID userProfileId);

    void deleteByUserProfileId(UUID userProfileId);
}
