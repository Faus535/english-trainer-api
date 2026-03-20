package com.faus535.englishtrainer.activity.domain;

import com.faus535.englishtrainer.activity.domain.event.ActivityRecordedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;

public final class ActivityDate extends AggregateRoot<ActivityDateId> {

    private final ActivityDateId id;
    private final UserProfileId userId;
    private final LocalDate activityDate;

    private ActivityDate(ActivityDateId id, UserProfileId userId, LocalDate activityDate) {
        this.id = id;
        this.userId = userId;
        this.activityDate = activityDate;
    }

    public static ActivityDate create(UserProfileId userId, LocalDate date) {
        ActivityDate activity = new ActivityDate(ActivityDateId.generate(), userId, date);
        activity.registerEvent(new ActivityRecordedEvent(userId, date));
        return activity;
    }

    public static ActivityDate reconstitute(ActivityDateId id, UserProfileId userId, LocalDate date) {
        return new ActivityDate(id, userId, date);
    }

    public ActivityDateId id() { return id; }
    public UserProfileId userId() { return userId; }
    public LocalDate activityDate() { return activityDate; }
}
