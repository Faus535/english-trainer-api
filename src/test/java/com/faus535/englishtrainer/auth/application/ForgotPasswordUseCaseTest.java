package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.error.TooManyResetAttemptsException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryPasswordResetTokenRepository;
import com.faus535.englishtrainer.auth.infrastructure.StubEmailPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForgotPasswordUseCaseTest {

    private InMemoryAuthUserRepository authUserRepository;
    private InMemoryPasswordResetTokenRepository tokenRepository;
    private StubEmailPort emailPort;
    private ForgotPasswordUseCase useCase;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        tokenRepository = new InMemoryPasswordResetTokenRepository();
        emailPort = new StubEmailPort();
        useCase = new ForgotPasswordUseCase(authUserRepository, tokenRepository, emailPort);
    }

    @Test
    void shouldSendResetEmailForValidUser() throws Exception {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        useCase.execute(user.email());

        assertEquals(1, emailPort.getCalls().size());
        assertEquals(user.email(), emailPort.getCalls().get(0).toEmail());
    }

    @Test
    void shouldSilentlySucceedForMissingEmail() throws Exception {
        useCase.execute("nonexistent@example.com");

        assertTrue(emailPort.getCalls().isEmpty());
    }

    @Test
    void shouldSilentlySucceedForGoogleAccount() throws Exception {
        AuthUser googleUser = AuthUserMother.googleUser();
        authUserRepository.save(googleUser);

        useCase.execute(googleUser.email());

        assertTrue(emailPort.getCalls().isEmpty());
    }

    @Test
    void shouldThrowTooManyAttemptsAfterThreeRequests() throws Exception {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        useCase.execute(user.email());
        useCase.execute(user.email());
        useCase.execute(user.email());

        assertThrows(TooManyResetAttemptsException.class,
                () -> useCase.execute(user.email()));
    }
}
