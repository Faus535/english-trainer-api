package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserProfileUseCase {

    private final UserProfileRepository repository;

    public GetUserProfileUseCase(UserProfileRepository repository) {
        this.repository = repository;
    }

    public UserProfile execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + id));
    }
}
