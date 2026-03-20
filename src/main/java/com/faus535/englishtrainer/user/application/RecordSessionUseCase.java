package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

@UseCase
public final class RecordSessionUseCase {

    private final UserProfileRepository repository;

    public RecordSessionUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfile execute(UserProfileId id) throws UserProfileNotFoundException {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
        return repository.save(profile.recordSession());
    }
}
