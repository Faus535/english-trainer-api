package com.faus535.englishtrainer.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

interface JpaPushSubscriptionRepository extends JpaRepository<PushSubscriptionEntity, UUID> {
    List<PushSubscriptionEntity> findByUserId(UUID userId);
    void deleteByEndpoint(String endpoint);
}
