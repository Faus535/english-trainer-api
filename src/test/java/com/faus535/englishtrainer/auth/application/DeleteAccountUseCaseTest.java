package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DeleteAccountUseCaseTest {

    private InMemoryAuthUserRepository authUserRepository;
    private InMemoryUserProfileRepository userProfileRepository;
    private PasswordEncoder passwordEncoder;
    private DeleteAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new DeleteAccountUseCase(authUserRepository, userProfileRepository, passwordEncoder);
    }

    @Test
    void shouldDeleteAccountWithCorrectPassword() throws Exception {
        String password = "correctPassword";
        UserProfileId profileId = UserProfileId.generate();
        AuthUser user = AuthUser.create("test@example.com", passwordEncoder.encode(password), profileId);
        authUserRepository.save(user);
        userProfileRepository.save(UserProfile.reconstitute(profileId, null, 0, null, Instant.now(), Instant.now()));

        useCase.execute(user.id().value().toString(), password);

        assertTrue(authUserRepository.findById(user.id()).isEmpty());
        assertTrue(userProfileRepository.findById(profileId).isEmpty());
    }

    @Test
    void shouldDeleteGoogleAccountWithoutPassword() throws Exception {
        AuthUser googleUser = AuthUserMother.googleUser();
        authUserRepository.save(googleUser);
        userProfileRepository.save(UserProfile.reconstitute(
                googleUser.userProfileId(), null, 0, null, Instant.now(), Instant.now()));

        useCase.execute(googleUser.id().value().toString(), null);

        assertTrue(authUserRepository.findById(googleUser.id()).isEmpty());
    }

    @Test
    void shouldThrowInvalidCredentialsForWrongPassword() {
        AuthUser user = AuthUser.create("test@example.com", passwordEncoder.encode("correct"),
                UserProfileId.generate());
        authUserRepository.save(user);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute(user.id().value().toString(), "wrongPassword"));
    }

    @Test
    void shouldThrowNotFoundForMissingUser() {
        assertThrows(NotFoundException.class,
                () -> useCase.execute(java.util.UUID.randomUUID().toString(), "pass"));
    }
}
