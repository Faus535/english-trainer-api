package com.faus535.englishtrainer.auth.infrastructure;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.RefreshTokenRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<UUID, RefreshToken> store = new HashMap<>();

    @Override
    public RefreshToken save(RefreshToken token) {
        store.put(token.id(), token);
        return token;
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return store.values().stream()
                .filter(t -> t.tokenHash().equals(tokenHash))
                .findFirst();
    }

    @Override
    public void revokeAllByUserId(AuthUserId userId) {
        store.replaceAll((id, token) -> {
            if (token.userId().equals(userId) && !token.revoked()) {
                return token.revoke();
            }
            return token;
        });
    }
}
