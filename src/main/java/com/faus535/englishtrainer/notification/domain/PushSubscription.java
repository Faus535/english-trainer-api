package com.faus535.englishtrainer.notification.domain;

import java.time.Instant;
import java.util.UUID;

public record PushSubscription(
        UUID id,
        UUID userId,
        String endpoint,
        String p256dh,
        String auth,
        Instant createdAt
) {

    public static PushSubscription create(UUID userId, String endpoint, String p256dh, String auth) {
        return new PushSubscription(UUID.randomUUID(), userId, endpoint, p256dh, auth, Instant.now());
    }
}
