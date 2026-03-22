package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.domain.error.AccountUsesGoogleException;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class ChangePasswordUseCase {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCase(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(String userId, String currentPassword, String newPassword)
            throws NotFoundException, InvalidCredentialsException, AccountUsesGoogleException {

        AuthUserId authUserId = AuthUserId.fromString(userId);
        AuthUser user = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (user.isGoogleAccount()) {
            throw new AccountUsesGoogleException();
        }

        if (!passwordEncoder.matches(currentPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        AuthUser updatedUser = user.withPasswordHash(encodedNewPassword);
        authUserRepository.save(updatedUser);
    }
}
