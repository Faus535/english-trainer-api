package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class ResetTestUseCase {

    private final UserProfileRepository repository;

    public ResetTestUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserProfile execute(UserProfileId id) throws UserProfileNotFoundException {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
        UserProfile updated = profile.resetTest();
        return repository.save(updated);
    }
}
