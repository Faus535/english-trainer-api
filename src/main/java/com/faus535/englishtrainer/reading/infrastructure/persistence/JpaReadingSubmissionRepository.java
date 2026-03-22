package com.faus535.englishtrainer.reading.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaReadingSubmissionRepository extends JpaRepository<ReadingSubmissionEntity, UUID> {

    List<ReadingSubmissionEntity> findByUserId(UUID userId);
}
