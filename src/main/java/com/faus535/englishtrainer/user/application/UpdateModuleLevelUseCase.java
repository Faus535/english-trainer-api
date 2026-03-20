package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserLevel;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

@UseCase
public final class UpdateModuleLevelUseCase {

    private final UserProfileRepository repository;

    public UpdateModuleLevelUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public void execute(UserProfileId id, String module, UserLevel level) throws UserProfileNotFoundException, InvalidModuleException {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
        repository.save(profile.updateModuleLevel(module, level));
    }
}
