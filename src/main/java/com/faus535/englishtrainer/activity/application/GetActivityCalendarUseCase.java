package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@UseCase
public final class GetActivityCalendarUseCase {

    private final ActivityDateRepository repository;

    public GetActivityCalendarUseCase(ActivityDateRepository repository) {
        this.repository = repository;
    }

    public List<LocalDate> execute(UserProfileId userId, int year, int month) {
        return repository.findByUserAndMonth(userId, year, month).stream()
                .map(ActivityDate::activityDate)
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
