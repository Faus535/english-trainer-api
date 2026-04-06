package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.activity.domain.StreakCalculator;
import com.faus535.englishtrainer.activity.domain.StreakInfo;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@UseCase
public class GetStreakUseCase {

    private final ActivityDateRepository repository;

    GetStreakUseCase(ActivityDateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public StreakInfo execute(UserProfileId userId) {
        List<LocalDate> sortedDatesDescending = repository.findAllByUser(userId).stream()
                .map(ActivityDate::activityDate)
                .sorted(java.util.Comparator.reverseOrder())
                .toList();

        return StreakCalculator.calculate(sortedDatesDescending);
    }
}
