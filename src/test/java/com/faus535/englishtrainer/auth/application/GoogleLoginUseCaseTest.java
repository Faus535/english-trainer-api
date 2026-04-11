package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.GoogleVerifiedUser;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.auth.infrastructure.StubGoogleAuthPort;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoogleLoginUseCaseTest {

    private StubGoogleAuthPort googleAuthPort;
    private InMemoryAuthUserRepository authUserRepository;
    private InMemoryUserProfileRepository userProfileRepository;
    private GoogleLoginUseCase useCase;

    @BeforeEach
    void setUp() {
        googleAuthPort = new StubGoogleAuthPort();
        authUserRepository = new InMemoryAuthUserRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        useCase = new GoogleLoginUseCase(googleAuthPort, authUserRepository, userProfileRepository);
    }

    @Test
    void shouldCreateNewUserWhenGoogleTokenIsValid() throws GoogleAuthException {
        googleAuthPort.willReturn(new GoogleVerifiedUser("new@google.com", "New User", true));

        AuthUser result = useCase.execute("valid-google-token");

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

        googleAuthPort.willReturn(new GoogleVerifiedUser("existing@google.com", "Existing User", true));

        AuthUser result = useCase.execute("valid-google-token");

        assertEquals(existingUser.id(), result.id());
        assertEquals(1, userProfileRepository.count());
    }

    @Test
    void shouldThrowWhenGoogleTokenIsInvalid() {
        googleAuthPort.willThrow(new GoogleAuthException("Invalid Google ID token"));

        assertThrows(GoogleAuthException.class, () -> useCase.execute("invalid-token"));
    }

    @Test
    void shouldThrowWhenEmailIsNotVerified() {
        googleAuthPort.willReturn(new GoogleVerifiedUser("unverified@google.com", "User", false));

        GoogleAuthException exception = assertThrows(
                GoogleAuthException.class, () -> useCase.execute("unverified-email-token"));
        assertEquals("Google email not verified", exception.getMessage());
    }

    @Test
    void shouldCreateMissingProfileForReturningGoogleUser() throws GoogleAuthException {
        UserProfileId orphanedProfileId = UserProfileId.generate();
        AuthUser orphanedUser = AuthUser.createFromGoogle("orphan@google.com", orphanedProfileId);
        authUserRepository.save(orphanedUser);

        googleAuthPort.willReturn(new GoogleVerifiedUser("orphan@google.com", "Orphan User", true));

        AuthUser result = useCase.execute("valid-google-token");

        assertEquals(orphanedUser.id(), result.id());
        assertTrue(userProfileRepository.findById(orphanedProfileId).isPresent());
    }

    @Test
    void shouldNotDuplicateProfileForReturningGoogleUserWithExistingProfile() throws GoogleAuthException {
        AuthUser existingUser = AuthUserMother.googleUserWithEmail("existing@google.com");
        authUserRepository.save(existingUser);
        var profile = com.faus535.englishtrainer.user.domain.UserProfile.reconstitute(
                existingUser.userProfileId(), null, 100, null, java.time.Instant.now(), java.time.Instant.now());
        userProfileRepository.save(profile);

        googleAuthPort.willReturn(new GoogleVerifiedUser("existing@google.com", "Existing User", true));

        AuthUser result = useCase.execute("valid-google-token");

        assertEquals(existingUser.id(), result.id());
        assertEquals(1, userProfileRepository.count());
        assertEquals(100, userProfileRepository.findById(existingUser.userProfileId()).get().xp());
    }
}
