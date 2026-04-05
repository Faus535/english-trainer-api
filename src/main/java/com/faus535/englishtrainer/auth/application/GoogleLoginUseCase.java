package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.domain.GoogleAuthPort;
import com.faus535.englishtrainer.auth.domain.GoogleVerifiedUser;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@UseCase
public class GoogleLoginUseCase {

    private final GoogleAuthPort googleAuthPort;
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;

    GoogleLoginUseCase(GoogleAuthPort googleAuthPort,
                       AuthUserRepository authUserRepository,
                       UserProfileRepository userProfileRepository) {
        this.googleAuthPort = googleAuthPort;
        this.authUserRepository = authUserRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public AuthUser execute(String idToken) throws GoogleAuthException {
        GoogleVerifiedUser googleUser = googleAuthPort.verify(idToken);

        if (!googleUser.emailVerified()) {
            throw new GoogleAuthException("Google email not verified");
        }

        Optional<AuthUser> existingUser = authUserRepository.findByEmail(googleUser.email());

        if (existingUser.isPresent()) {
            AuthUser user = existingUser.get();
            if (user.userProfileId() != null) {
                userProfileRepository.findById(user.userProfileId())
                        .orElseGet(() -> {
                            UserProfile repair = UserProfile.reconstitute(
                                    user.userProfileId(), null, 0, Instant.now(), Instant.now());
                            return userProfileRepository.save(repair);
                        });
            }
            return user;
        }

        UserProfile profile = UserProfile.create();
        UserProfile savedProfile = userProfileRepository.save(profile);

        AuthUser newUser = AuthUser.createFromGoogle(googleUser.email(), savedProfile.id());
        return authUserRepository.save(newUser);
    }
}
