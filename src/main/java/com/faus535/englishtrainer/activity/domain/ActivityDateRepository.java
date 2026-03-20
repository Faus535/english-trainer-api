package com.faus535.englishtrainer.activity.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActivityDateRepository {
    Optional<ActivityDate> findByUserAndDate(UserProfileId userId, LocalDate date);
    List<ActivityDate> findAllByUser(UserProfileId userId);
    List<ActivityDate> findByUserAndMonth(UserProfileId userId, int year, int month);
    ActivityDate save(ActivityDate activityDate);
}
