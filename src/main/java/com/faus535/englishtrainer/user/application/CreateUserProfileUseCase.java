package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;

@UseCase
public final class CreateUserProfileUseCase {

    private final UserProfileRepository repository;

    public CreateUserProfileUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfile execute() {
        UserProfile profile = UserProfile.create();
        return repository.save(profile);
    }
}
