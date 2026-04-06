package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.RefreshTokenRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@UseCase
public class LogoutUserUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    LogoutUserUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void execute(String refreshTokenHash) {
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByTokenHash(refreshTokenHash);

        storedToken.ifPresent(token -> {
            if (!token.revoked()) {
                refreshTokenRepository.save(token.revoke());
            }
        });
    }
}
