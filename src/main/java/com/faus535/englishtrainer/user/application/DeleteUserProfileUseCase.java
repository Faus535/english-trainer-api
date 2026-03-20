package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

@UseCase
public final class DeleteUserProfileUseCase {

    private final UserProfileRepository repository;

    public DeleteUserProfileUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(UserProfileId id) throws UserProfileNotFoundException {
        repository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
        repository.deleteById(id);
    }
}
