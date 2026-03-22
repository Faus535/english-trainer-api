package com.faus535.englishtrainer.notification.application;

import com.faus535.englishtrainer.notification.domain.PushSubscription;
import com.faus535.englishtrainer.notification.domain.PushSubscriptionRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class SubscribePushUseCase {

    private final PushSubscriptionRepository repository;

    public SubscribePushUseCase(PushSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(UUID userId, String endpoint, String p256dh, String auth) {
        PushSubscription subscription = PushSubscription.create(userId, endpoint, p256dh, auth);
        repository.save(subscription);
    }
}
