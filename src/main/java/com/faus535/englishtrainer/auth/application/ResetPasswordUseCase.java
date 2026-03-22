package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.*;
import com.faus535.englishtrainer.auth.domain.error.InvalidResetTokenException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class ResetPasswordUseCase {

    private final PasswordResetTokenRepository tokenRepository;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordUseCase(PasswordResetTokenRepository tokenRepository,
                                 AuthUserRepository authUserRepository,
                                 PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(String rawToken, String newPassword) throws InvalidResetTokenException, NotFoundException {
        String tokenHash = ForgotPasswordUseCase.hashToken(rawToken);

        PasswordResetToken resetToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidResetTokenException::new);

        if (!resetToken.isValid()) {
            throw new InvalidResetTokenException();
        }

        AuthUser user = authUserRepository.findById(resetToken.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        AuthUser updatedUser = user.withPasswordHash(encodedPassword);
        authUserRepository.save(updatedUser);

        tokenRepository.save(resetToken.markUsed());
    }
}
