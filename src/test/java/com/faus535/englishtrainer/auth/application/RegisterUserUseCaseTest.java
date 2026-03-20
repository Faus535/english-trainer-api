package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.error.EmailAlreadyExistsException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

final class RegisterUserUseCaseTest {

    private InMemoryAuthUserRepository authUserRepository;
    private InMemoryUserProfileRepository userProfileRepository;
    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        PasswordEncoder encoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return "encoded_" + rawPassword;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals("encoded_" + rawPassword);
            }
        };
        useCase = new RegisterUserUseCase(authUserRepository, userProfileRepository, encoder);
    }

    @Test
    void shouldRegisterNewUser() throws EmailAlreadyExistsException {
        AuthUser user = useCase.execute("test@example.com", "password123");

        assertNotNull(user);
        assertEquals("test@example.com", user.email());
        assertNotNull(user.userProfileId());
        assertEquals(1, userProfileRepository.count());
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() throws EmailAlreadyExistsException {
        useCase.execute("test@example.com", "password123");

        assertThrows(EmailAlreadyExistsException.class,
                () -> useCase.execute("test@example.com", "password456"));
    }
}
