package com.faus535.englishtrainer.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaUserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {
}
