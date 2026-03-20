package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.domain.error.EmailAlreadyExistsException;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@UseCase
public final class RegisterUserUseCase {

    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(AuthUserRepository authUserRepository,
                               UserProfileRepository userProfileRepository,
                               PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUser execute(String email, String password) throws EmailAlreadyExistsException {
        if (authUserRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        UserProfile profile = UserProfile.create();
        UserProfile savedProfile = userProfileRepository.save(profile);

        String encodedPassword = passwordEncoder.encode(password);
        AuthUser user = AuthUser.create(email, encodedPassword, savedProfile.id());

        return authUserRepository.save(user);
    }
}
