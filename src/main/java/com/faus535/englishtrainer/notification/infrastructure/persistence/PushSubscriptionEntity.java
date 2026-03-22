package com.faus535.englishtrainer.notification.infrastructure.persistence;

import com.faus535.englishtrainer.notification.domain.PushSubscription;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "push_subscriptions")
class PushSubscriptionEntity implements Persistable<UUID> {

    @Id private UUID id;
    @Transient private boolean isNew;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(nullable = false, columnDefinition = "TEXT") private String endpoint;
    @Column(nullable = false) private String p256dh;
    @Column(nullable = false) private String auth;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected PushSubscriptionEntity() {}

    static PushSubscriptionEntity fromDomain(PushSubscription sub) {
        PushSubscriptionEntity e = new PushSubscriptionEntity();
        e.id = sub.id(); e.isNew = true; e.userId = sub.userId();
        e.endpoint = sub.endpoint(); e.p256dh = sub.p256dh();
        e.auth = sub.auth(); e.createdAt = sub.createdAt();
        return e;
    }

    PushSubscription toDomain() {
        return new PushSubscription(id, userId, endpoint, p256dh, auth, createdAt);
    }

    @Override public UUID getId() { return id; }
    @Override public boolean isNew() { return isNew; }
}
