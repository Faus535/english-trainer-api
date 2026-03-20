package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@UseCase
public class GetUserProfileUseCase {

    private final UserProfileRepository repository;

    public GetUserProfileUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public UserProfile execute(UserProfileId id) throws UserProfileNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
    }
}
