package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class LogoutUserUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutUserUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(String refreshTokenHash) {
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByTokenHash(refreshTokenHash);

        storedToken.ifPresent(token -> {
            if (!token.revoked()) {
                refreshTokenRepository.save(token.revoke());
            }
        });
    }
}
