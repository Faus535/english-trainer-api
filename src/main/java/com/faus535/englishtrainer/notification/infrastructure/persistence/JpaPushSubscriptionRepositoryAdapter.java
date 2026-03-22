package com.faus535.englishtrainer.notification.infrastructure.persistence;

import com.faus535.englishtrainer.notification.domain.PushSubscription;
import com.faus535.englishtrainer.notification.domain.PushSubscriptionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
class JpaPushSubscriptionRepositoryAdapter implements PushSubscriptionRepository {

    private final JpaPushSubscriptionRepository jpaRepository;

    JpaPushSubscriptionRepositoryAdapter(JpaPushSubscriptionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PushSubscription save(PushSubscription subscription) {
        return jpaRepository.save(PushSubscriptionEntity.fromDomain(subscription)).toDomain();
    }

    @Override
    public List<PushSubscription> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(PushSubscriptionEntity::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteByEndpoint(String endpoint) {
        jpaRepository.deleteByEndpoint(endpoint);
    }
}
