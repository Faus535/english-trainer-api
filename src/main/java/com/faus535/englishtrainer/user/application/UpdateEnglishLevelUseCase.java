package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.domain.vo.EnglishLevel;

import org.springframework.transaction.annotation.Transactional;

@UseCase
public class UpdateEnglishLevelUseCase {

    private final UserProfileRepository repository;

    public UpdateEnglishLevelUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(UserProfileId id, EnglishLevel level) throws UserProfileNotFoundException {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
        UserProfile updated = profile.updateEnglishLevel(level);
        repository.save(updated);
    }
}
