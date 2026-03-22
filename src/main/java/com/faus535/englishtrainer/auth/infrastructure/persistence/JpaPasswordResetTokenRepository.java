package com.faus535.englishtrainer.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface JpaPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {

    Optional<PasswordResetTokenEntity> findByTokenHash(String tokenHash);

    @Query("SELECT COUNT(p) FROM PasswordResetTokenEntity p WHERE p.userId = :userId AND p.createdAt > :since")
    int countRecentByUserId(UUID userId, Instant since);
}
