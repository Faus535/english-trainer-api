package com.faus535.englishtrainer.session.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaSessionRepository extends JpaRepository<SessionEntity, UUID> {

    Optional<SessionEntity> findByUserIdAndCompletedFalse(UUID userId);

    List<SessionEntity> findByUserIdOrderByStartedAtDesc(UUID userId);
}
