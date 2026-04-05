package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.error.AccountUsesGoogleException;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordUseCaseTest {

    private InMemoryAuthUserRepository authUserRepository;
    private PasswordEncoder passwordEncoder;
    private ChangePasswordUseCase useCase;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new ChangePasswordUseCase(authUserRepository, passwordEncoder);
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        String currentPassword = "oldPassword123";
        String encodedPassword = passwordEncoder.encode(currentPassword);
        AuthUser user = AuthUser.create("test@example.com", encodedPassword,
                com.faus535.englishtrainer.user.domain.UserProfileId.generate());
        authUserRepository.save(user);

        useCase.execute(user.id().value().toString(), currentPassword, "newPassword456");

        AuthUser updated = authUserRepository.findById(user.id()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword456", updated.passwordHash()));
    }

    @Test
    void shouldThrowNotFoundForMissingUser() {
        assertThrows(NotFoundException.class,
                () -> useCase.execute(java.util.UUID.randomUUID().toString(), "pass", "newPass"));
    }

    @Test
    void shouldThrowInvalidCredentialsForWrongPassword() {
        String encodedPassword = passwordEncoder.encode("correctPassword");
        AuthUser user = AuthUser.create("test@example.com", encodedPassword,
                com.faus535.englishtrainer.user.domain.UserProfileId.generate());
        authUserRepository.save(user);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute(user.id().value().toString(), "wrongPassword", "newPass"));
    }

    @Test
    void shouldThrowAccountUsesGoogleForGoogleUser() {
        AuthUser googleUser = AuthUserMother.googleUser();
        authUserRepository.save(googleUser);

        assertThrows(AccountUsesGoogleException.class,
                () -> useCase.execute(googleUser.id().value().toString(), "any", "newPass"));
    }
}
