package com.faus535.englishtrainer.activity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaActivityDateRepository extends JpaRepository<ActivityDateEntity, UUID> {
    Optional<ActivityDateEntity> findByUserIdAndActivityDate(UUID userId, LocalDate date);
    List<ActivityDateEntity> findByUserIdOrderByActivityDateDesc(UUID userId);
    List<ActivityDateEntity> findByUserIdAndActivityDateBetween(UUID userId, LocalDate start, LocalDate end);
}
