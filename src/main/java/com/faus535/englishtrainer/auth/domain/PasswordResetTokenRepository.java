package com.faus535.englishtrainer.auth.domain;

import java.util.Optional;

public interface PasswordResetTokenRepository {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    int countRecentByUserId(AuthUserId userId, int minutes);
}
