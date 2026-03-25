package com.faus535.englishtrainer.user.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface JpaUserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM UserProfileEntity p WHERE p.id = :id")
    Optional<UserProfileEntity> findByIdForUpdate(@Param("id") UUID id);
}
