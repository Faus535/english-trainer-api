package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;

public final class UserProfileMother {

    public static UserProfile create() {
        return UserProfile.create();
    }

    public static UserProfile withXp(int xp) throws InvalidXpAmountException {
        return UserProfile.create().addXp(xp);
    }

    public static UserProfile withTestCompleted() {
        return UserProfile.create().markTestCompleted();
    }
}
