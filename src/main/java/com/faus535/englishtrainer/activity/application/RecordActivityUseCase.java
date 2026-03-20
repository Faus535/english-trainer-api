package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@UseCase
public class RecordActivityUseCase {

    private final ActivityDateRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public RecordActivityUseCase(ActivityDateRepository repository,
                                 ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ActivityDate execute(UserProfileId userId, LocalDate date) {
        Optional<ActivityDate> existing = repository.findByUserAndDate(userId, date);
        if (existing.isPresent()) {
            return existing.get();
        }

        ActivityDate activityDate = ActivityDate.create(userId, date);
        ActivityDate saved = repository.save(activityDate);
        activityDate.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
