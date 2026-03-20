package com.faus535.englishtrainer.activity.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;

public final class ActivityDateMother {

    private ActivityDateMother() {}

    public static ActivityDate create() {
        return ActivityDate.create(UserProfileId.generate(), LocalDate.now());
    }

    public static ActivityDate create(UserProfileId userId, LocalDate date) {
        return ActivityDate.create(userId, date);
    }
}
