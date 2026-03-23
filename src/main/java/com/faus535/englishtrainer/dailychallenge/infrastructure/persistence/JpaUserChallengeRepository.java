package com.faus535.englishtrainer.dailychallenge.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaUserChallengeRepository extends JpaRepository<UserChallengeEntity, UUID> {

    Optional<UserChallengeEntity> findByUserIdAndChallengeId(UUID userId, UUID challengeId);
}
