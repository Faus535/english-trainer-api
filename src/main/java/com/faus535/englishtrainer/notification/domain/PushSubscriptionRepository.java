package com.faus535.englishtrainer.notification.domain;

import java.util.List;
import java.util.UUID;

public interface PushSubscriptionRepository {

    PushSubscription save(PushSubscription subscription);

    List<PushSubscription> findByUserId(UUID userId);

    void deleteByEndpoint(String endpoint);
}
