package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.*;
import com.faus535.englishtrainer.auth.domain.error.TooManyResetAttemptsException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@UseCase
public class ForgotPasswordUseCase {

    private static final int MAX_ATTEMPTS_PER_HOUR = 3;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AuthUserRepository authUserRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailPort emailPort;

    public ForgotPasswordUseCase(AuthUserRepository authUserRepository,
                                  PasswordResetTokenRepository tokenRepository,
                                  EmailPort emailPort) {
        this.authUserRepository = authUserRepository;
        this.tokenRepository = tokenRepository;
        this.emailPort = emailPort;
    }

    @Transactional
    public void execute(String email) throws TooManyResetAttemptsException {
        Optional<AuthUser> userOpt = authUserRepository.findByEmail(email);

        // Always return success to prevent email enumeration
        if (userOpt.isEmpty() || userOpt.get().isGoogleAccount()) {
            return;
        }

        AuthUser user = userOpt.get();

        int recentAttempts = tokenRepository.countRecentByUserId(user.id(), 60);
        if (recentAttempts >= MAX_ATTEMPTS_PER_HOUR) {
            throw new TooManyResetAttemptsException();
        }

        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.create(user.id(), tokenHash);
        tokenRepository.save(resetToken);

        emailPort.sendPasswordResetEmail(email, rawToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
