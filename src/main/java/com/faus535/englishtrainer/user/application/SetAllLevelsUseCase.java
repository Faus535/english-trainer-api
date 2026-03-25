package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserLevel;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@UseCase
public class SetAllLevelsUseCase {

    private final UserProfileRepository repository;

    public SetAllLevelsUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(UserProfileId id, Map<String, UserLevel> levels)
            throws UserProfileNotFoundException, InvalidModuleException {
        UserProfile profile = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));

        for (var entry : levels.entrySet()) {
            profile = profile.updateModuleLevel(entry.getKey(), entry.getValue());
        }
        profile = profile.markTestCompleted();

        repository.save(profile);
    }
}
