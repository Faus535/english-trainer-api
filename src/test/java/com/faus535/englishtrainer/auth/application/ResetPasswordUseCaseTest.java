package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.PasswordResetToken;
import com.faus535.englishtrainer.auth.domain.error.InvalidResetTokenException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryPasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordUseCaseTest {

    private InMemoryPasswordResetTokenRepository tokenRepository;
    private InMemoryAuthUserRepository authUserRepository;
    private PasswordEncoder passwordEncoder;
    private ResetPasswordUseCase useCase;

    @BeforeEach
    void setUp() {
        tokenRepository = new InMemoryPasswordResetTokenRepository();
        authUserRepository = new InMemoryAuthUserRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new ResetPasswordUseCase(tokenRepository, authUserRepository, passwordEncoder);
    }

    @Test
    void shouldResetPasswordWithValidToken() throws Exception {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        String rawToken = "test-raw-token";
        String tokenHash = ForgotPasswordUseCase.hashToken(rawToken);
        PasswordResetToken resetToken = PasswordResetToken.create(user.id(), tokenHash);
        tokenRepository.save(resetToken);

        useCase.execute(rawToken, "newPassword123");

        AuthUser updated = authUserRepository.findById(user.id()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword123", updated.passwordHash()));
        assertTrue(tokenRepository.findByTokenHash(tokenHash).get().used());
    }

    @Test
    void shouldThrowInvalidResetTokenForMissingToken() {
        assertThrows(InvalidResetTokenException.class,
                () -> useCase.execute("nonexistent-token", "newPass"));
    }

    @Test
    void shouldThrowInvalidResetTokenForExpiredToken() {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        String rawToken = "expired-token";
        String tokenHash = ForgotPasswordUseCase.hashToken(rawToken);
        PasswordResetToken expiredToken = PasswordResetToken.reconstitute(
                UUID.randomUUID(), user.id(), tokenHash,
                Instant.now().minusSeconds(3600), false, Instant.now().minusSeconds(7200));
        tokenRepository.save(expiredToken);

        assertThrows(InvalidResetTokenException.class,
                () -> useCase.execute(rawToken, "newPass"));
    }

    @Test
    void shouldThrowInvalidResetTokenForUsedToken() {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        String rawToken = "used-token";
        String tokenHash = ForgotPasswordUseCase.hashToken(rawToken);
        PasswordResetToken usedToken = PasswordResetToken.create(user.id(), tokenHash).markUsed();
        tokenRepository.save(usedToken);

        assertThrows(InvalidResetTokenException.class,
                () -> useCase.execute(rawToken, "newPass"));
    }
}
