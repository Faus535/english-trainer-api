package com.faus535.englishtrainer.analytics.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class GetActivityHeatmapUseCase {

    private final ActivityDateRepository repository;

    public GetActivityHeatmapUseCase(ActivityDateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Map<String, Boolean> execute(UUID userId) {
        UserProfileId profileId = new UserProfileId(userId);
        return repository.findAllByUser(profileId).stream()
                .collect(Collectors.toMap(
                        d -> d.activityDate().toString(),
                        d -> true,
                        (a, b) -> a));
    }
}
