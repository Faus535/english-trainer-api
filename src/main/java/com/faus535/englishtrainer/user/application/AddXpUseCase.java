package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class AddXpUseCase {

    private final UserProfileRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public AddXpUseCase(UserProfileRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UserProfile execute(UserProfileId id, int amount) throws UserProfileNotFoundException, InvalidXpAmountException {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
        UserProfile updated = profile.addXp(amount);
        UserProfile saved = repository.save(updated);
        updated.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
