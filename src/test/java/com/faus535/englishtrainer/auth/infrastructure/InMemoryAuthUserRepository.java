package com.faus535.englishtrainer.auth.infrastructure;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class InMemoryAuthUserRepository implements AuthUserRepository {

    private final Map<AuthUserId, AuthUser> store = new HashMap<>();

    @Override
    public Optional<AuthUser> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.email().equals(email))
                .findFirst();
    }

    @Override
    public Optional<AuthUser> findById(AuthUserId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(u -> u.email().equals(email));
    }

    @Override
    public AuthUser save(AuthUser user) {
        store.put(user.id(), user);
        return user;
    }

    @Override
    public void deleteById(AuthUserId id) {
        store.remove(id);
    }
}
