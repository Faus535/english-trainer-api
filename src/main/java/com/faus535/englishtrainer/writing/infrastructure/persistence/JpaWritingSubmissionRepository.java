package com.faus535.englishtrainer.writing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaWritingSubmissionRepository extends JpaRepository<WritingSubmissionEntity, UUID> {

    List<WritingSubmissionEntity> findByUserId(UUID userId);
}
