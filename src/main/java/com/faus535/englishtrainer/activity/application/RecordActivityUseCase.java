package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.Optional;

@UseCase
public final class RecordActivityUseCase {

    private final ActivityDateRepository repository;

    public RecordActivityUseCase(ActivityDateRepository repository) {
        this.repository = repository;
    }

    public ActivityDate execute(UserProfileId userId, LocalDate date) {
        Optional<ActivityDate> existing = repository.findByUserAndDate(userId, date);
        if (existing.isPresent()) {
            return existing.get();
        }

        ActivityDate activityDate = ActivityDate.create(userId, date);
        return repository.save(activityDate);
    }
}
