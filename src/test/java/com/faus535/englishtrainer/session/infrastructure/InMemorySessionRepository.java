package com.faus535.englishtrainer.session.infrastructure;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.*;

public final class InMemorySessionRepository implements SessionRepository {

    private final Map<UUID, Session> store = new HashMap<>();

    @Override
    public Optional<Session> findById(SessionId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public Optional<Session> findActiveByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(s -> s.userId().equals(userId) && !s.completed())
                .findFirst();
    }

    @Override
    public List<Session> findByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(s -> s.userId().equals(userId))
                .toList();
    }

    @Override
    public Session save(Session session) {
        store.put(session.id().value(), session);
        return session;
    }
}
