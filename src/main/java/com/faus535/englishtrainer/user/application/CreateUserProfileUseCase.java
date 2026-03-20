package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CreateUserProfileUseCase {

    private final UserProfileRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateUserProfileUseCase(UserProfileRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UserProfile execute() {
        UserProfile profile = UserProfile.create();
        UserProfile saved = repository.save(profile);
        profile.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
