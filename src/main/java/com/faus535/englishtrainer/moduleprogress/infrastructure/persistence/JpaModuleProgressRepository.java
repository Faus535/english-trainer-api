package com.faus535.englishtrainer.moduleprogress.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaModuleProgressRepository extends JpaRepository<ModuleProgressEntity, UUID> {

    Optional<ModuleProgressEntity> findByUserIdAndModuleNameAndLevel(UUID userId, String moduleName, String level);

    List<ModuleProgressEntity> findByUserId(UUID userId);
}
