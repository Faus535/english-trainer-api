package com.faus535.englishtrainer.auth.infrastructure;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.PasswordResetToken;
import com.faus535.englishtrainer.auth.domain.PasswordResetTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryPasswordResetTokenRepository implements PasswordResetTokenRepository {

    private final Map<UUID, PasswordResetToken> store = new HashMap<>();

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        store.put(token.id(), token);
        return token;
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return store.values().stream()
                .filter(t -> t.tokenHash().equals(tokenHash))
                .findFirst();
    }

    @Override
    public int countRecentByUserId(AuthUserId userId, int minutes) {
        Instant cutoff = Instant.now().minus(minutes, ChronoUnit.MINUTES);
        return (int) store.values().stream()
                .filter(t -> t.userId().equals(userId))
                .filter(t -> t.createdAt().isAfter(cutoff))
                .count();
    }
}
