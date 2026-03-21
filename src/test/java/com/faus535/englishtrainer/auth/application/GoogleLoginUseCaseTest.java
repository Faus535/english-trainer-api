package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.auth.infrastructure.google.GoogleTokenVerifier;
import com.faus535.englishtrainer.auth.infrastructure.google.GoogleUserInfo;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoogleLoginUseCaseTest {

    private GoogleTokenVerifier googleTokenVerifier;
    private InMemoryAuthUserRepository authUserRepository;
    private InMemoryUserProfileRepository userProfileRepository;
    private GoogleLoginUseCase useCase;

    @BeforeEach
    void setUp() {
        googleTokenVerifier = mock(GoogleTokenVerifier.class);
        authUserRepository = new InMemoryAuthUserRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        useCase = new GoogleLoginUseCase(googleTokenVerifier, authUserRepository, userProfileRepository);
    }

    @Test
    void shouldCreateNewUserWhenGoogleTokenIsValid() throws GoogleAuthException {
        String idToken = "valid-google-token";
        when(googleTokenVerifier.verify(idToken))
                .thenReturn(new GoogleUserInfo("new@google.com", "New User", true));

        AuthUser result = useCase.execute(idToken);

        assertNotNull(result);
        assertEquals("new@google.com", result.email());
        assertEquals("GOOGLE", result.authProvider());
        assertNull(result.passwordHash());
        assertEquals(1, userProfileRepository.count());
    }

    @Test
    void shouldReturnExistingUserWhenEmailAlreadyExists() throws GoogleAuthException {
        AuthUser existingUser = AuthUserMother.withEmail("existing@google.com");
        authUserRepository.save(existingUser);

        String idToken = "valid-google-token";
        when(googleTokenVerifier.verify(idToken))
                .thenReturn(new GoogleUserInfo("existing@google.com", "Existing User", true));

        AuthUser result = useCase.execute(idToken);

        assertEquals(existingUser.id(), result.id());
        assertEquals(0, userProfileRepository.count());
    }

    @Test
    void shouldThrowWhenGoogleTokenIsInvalid() throws GoogleAuthException {
        String idToken = "invalid-token";
        when(googleTokenVerifier.verify(idToken))
                .thenThrow(new GoogleAuthException("Invalid Google ID token"));

        assertThrows(GoogleAuthException.class, () -> useCase.execute(idToken));
    }

    @Test
    void shouldThrowWhenEmailIsNotVerified() throws GoogleAuthException {
        String idToken = "unverified-email-token";
        when(googleTokenVerifier.verify(idToken))
                .thenReturn(new GoogleUserInfo("unverified@google.com", "User", false));

        GoogleAuthException exception = assertThrows(
                GoogleAuthException.class, () -> useCase.execute(idToken));
        assertEquals("Google email not verified", exception.getMessage());
    }
}
