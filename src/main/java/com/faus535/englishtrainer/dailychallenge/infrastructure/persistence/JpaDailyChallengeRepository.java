package com.faus535.englishtrainer.dailychallenge.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

interface JpaDailyChallengeRepository extends JpaRepository<DailyChallengeEntity, UUID> {

    Optional<DailyChallengeEntity> findByChallengeDate(LocalDate challengeDate);
}
