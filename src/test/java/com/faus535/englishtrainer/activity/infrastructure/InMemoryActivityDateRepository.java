package com.faus535.englishtrainer.activity.infrastructure;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.*;

public final class InMemoryActivityDateRepository implements ActivityDateRepository {

    private final Map<UUID, ActivityDate> store = new HashMap<>();

    @Override
    public Optional<ActivityDate> findByUserAndDate(UserProfileId userId, LocalDate date) {
        return store.values().stream()
                .filter(a -> a.userId().equals(userId) && a.activityDate().equals(date))
                .findFirst();
    }

    @Override
    public List<ActivityDate> findAllByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(a -> a.userId().equals(userId))
                .toList();
    }

    @Override
    public List<ActivityDate> findByUserAndMonth(UserProfileId userId, int year, int month) {
        return store.values().stream()
                .filter(a -> a.userId().equals(userId)
                        && a.activityDate().getYear() == year
                        && a.activityDate().getMonthValue() == month)
                .toList();
    }

    @Override
    public ActivityDate save(ActivityDate activityDate) {
        store.put(activityDate.id().value(), activityDate);
        return activityDate;
    }
}
