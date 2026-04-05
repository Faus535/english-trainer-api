package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaImmerseExerciseRepository extends JpaRepository<ImmerseExerciseEntity, UUID> {

    List<ImmerseExerciseEntity> findByContentIdOrderByOrderIndexAsc(UUID contentId);
}
