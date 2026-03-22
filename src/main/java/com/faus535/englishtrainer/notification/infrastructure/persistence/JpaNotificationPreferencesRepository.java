package com.faus535.englishtrainer.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface JpaNotificationPreferencesRepository extends JpaRepository<NotificationPreferencesEntity, UUID> {
    Optional<NotificationPreferencesEntity> findByUserId(UUID userId);
}
