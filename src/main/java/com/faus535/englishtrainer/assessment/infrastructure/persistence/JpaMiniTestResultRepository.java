package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaMiniTestResultRepository extends JpaRepository<MiniTestResultEntity, UUID> {

    List<MiniTestResultEntity> findByUserId(UUID userId);

    List<MiniTestResultEntity> findByUserIdAndModuleName(UUID userId, String moduleName);
}
