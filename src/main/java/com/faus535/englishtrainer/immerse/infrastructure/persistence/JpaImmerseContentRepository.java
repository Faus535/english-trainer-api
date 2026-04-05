package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaImmerseContentRepository extends JpaRepository<ImmerseContentEntity, UUID> {

    Page<ImmerseContentEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<ImmerseContentEntity> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);
}
