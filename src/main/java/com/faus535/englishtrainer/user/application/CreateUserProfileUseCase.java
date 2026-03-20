package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateUserProfileUseCase {

    private final UserProfileRepository repository;

    public CreateUserProfileUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfile execute() {
        return repository.save(UserProfile.create());
    }
}
