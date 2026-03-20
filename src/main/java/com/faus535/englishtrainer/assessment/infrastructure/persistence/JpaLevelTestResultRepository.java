package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaLevelTestResultRepository extends JpaRepository<LevelTestResultEntity, UUID> {

    List<LevelTestResultEntity> findByUserId(UUID userId);
}
