package com.faus535.englishtrainer.notification.infrastructure.persistence;

import com.faus535.englishtrainer.notification.domain.NotificationPreferences;
import com.faus535.englishtrainer.notification.domain.NotificationPreferencesRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class JpaNotificationPreferencesRepositoryAdapter implements NotificationPreferencesRepository {

    private final JpaNotificationPreferencesRepository jpaRepository;

    JpaNotificationPreferencesRepositoryAdapter(JpaNotificationPreferencesRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NotificationPreferences save(NotificationPreferences prefs) {
        NotificationPreferencesEntity entity = NotificationPreferencesEntity.fromDomain(prefs);
        if (jpaRepository.findByUserId(prefs.userId()).isPresent()) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<NotificationPreferences> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(NotificationPreferencesEntity::toDomain);
    }
}
