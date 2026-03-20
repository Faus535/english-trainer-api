package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class GetCurrentUserUseCase {

    private final AuthUserRepository authUserRepository;

    public GetCurrentUserUseCase(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Transactional(readOnly = true)
    public AuthUser execute(AuthUserId id) throws NotFoundException {
        return authUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id.value()));
    }
}
