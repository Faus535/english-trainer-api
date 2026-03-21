package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.auth.infrastructure.google.GoogleTokenVerifier;
import com.faus535.englishtrainer.auth.infrastructure.google.GoogleUserInfo;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@UseCase
public class GoogleLoginUseCase {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;

    public GoogleLoginUseCase(GoogleTokenVerifier googleTokenVerifier,
                              AuthUserRepository authUserRepository,
                              UserProfileRepository userProfileRepository) {
        this.googleTokenVerifier = googleTokenVerifier;
        this.authUserRepository = authUserRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public AuthUser execute(String idToken) throws GoogleAuthException {
        GoogleUserInfo googleUser = googleTokenVerifier.verify(idToken);

        if (!googleUser.emailVerified()) {
            throw new GoogleAuthException("Google email not verified");
        }

        Optional<AuthUser> existingUser = authUserRepository.findByEmail(googleUser.email());

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        UserProfile profile = UserProfile.create();
        UserProfile savedProfile = userProfileRepository.save(profile);

        AuthUser newUser = AuthUser.createFromGoogle(googleUser.email(), savedProfile.id());
        return authUserRepository.save(newUser);
    }
}
