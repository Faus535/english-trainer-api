package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class DeleteAccountUseCase {

    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public DeleteAccountUseCase(AuthUserRepository authUserRepository,
                                UserProfileRepository userProfileRepository,
                                PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(String userId, String password)
            throws NotFoundException, InvalidCredentialsException {

        AuthUserId authUserId = AuthUserId.fromString(userId);
        AuthUser user = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (!user.isGoogleAccount()) {
            if (password == null || !passwordEncoder.matches(password, user.passwordHash())) {
                throw new InvalidCredentialsException();
            }
        }

        userProfileRepository.deleteById(user.userProfileId());
        authUserRepository.deleteById(authUserId);
    }
}
