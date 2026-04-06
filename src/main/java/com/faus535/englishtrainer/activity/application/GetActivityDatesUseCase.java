package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@UseCase
public class GetActivityDatesUseCase {

    private final ActivityDateRepository repository;

    GetActivityDatesUseCase(ActivityDateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<LocalDate> execute(UserProfileId userId) {
        return repository.findAllByUser(userId).stream()
                .map(ActivityDate::activityDate)
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
