package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.error.AccountUsesGoogleException;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginUserUseCaseTest {

    private InMemoryAuthUserRepository authUserRepository;
    private PasswordEncoder passwordEncoder;
    private LoginUserUseCase useCase;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new LoginUserUseCase(authUserRepository, passwordEncoder);
    }

    @Test
    void shouldLoginWithValidCredentials() throws InvalidCredentialsException, AccountUsesGoogleException {
        String encodedPassword = passwordEncoder.encode("password123");
        AuthUser user = AuthUser.create("user@test.com", encodedPassword, com.faus535.englishtrainer.user.domain.UserProfileId.generate());
        authUserRepository.save(user);

        AuthUser result = useCase.execute("user@test.com", "password123");

        assertEquals("user@test.com", result.email());
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute("nonexistent@test.com", "password123"));
    }

    @Test
    void shouldThrowWhenPasswordIsWrong() {
        AuthUser user = AuthUserMother.withEmail("user@test.com");
        authUserRepository.save(user);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute("user@test.com", "wrongpassword"));
    }

    @Test
    void shouldThrowWhenAccountUsesGoogle() {
        AuthUser googleUser = AuthUserMother.googleUserWithEmail("google@test.com");
        authUserRepository.save(googleUser);

        assertThrows(AccountUsesGoogleException.class,
                () -> useCase.execute("google@test.com", "anypassword"));
    }
}
