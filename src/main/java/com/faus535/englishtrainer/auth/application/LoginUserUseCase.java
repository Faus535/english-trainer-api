package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import org.springframework.security.crypto.password.PasswordEncoder;

@UseCase
public final class LoginUserUseCase {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginUserUseCase(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUser execute(String email, String password) throws InvalidCredentialsException {
        AuthUser user = authUserRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
