package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaImmerseSubmissionRepository extends JpaRepository<ImmerseSubmissionEntity, UUID> {
}
