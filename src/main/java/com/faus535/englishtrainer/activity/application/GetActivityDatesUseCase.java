package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@UseCase
public final class GetActivityDatesUseCase {

    private final ActivityDateRepository repository;

    public GetActivityDatesUseCase(ActivityDateRepository repository) {
        this.repository = repository;
    }

    public List<LocalDate> execute(UserProfileId userId) {
        return repository.findAllByUser(userId).stream()
                .map(ActivityDate::activityDate)
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
