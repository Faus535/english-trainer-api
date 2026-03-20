package com.faus535.englishtrainer.activity.domain;

import com.faus535.englishtrainer.activity.domain.event.ActivityRecordedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

final class ActivityDateTest {

    @Test
    void shouldCreateActivityDate() {
        UserProfileId userId = UserProfileId.generate();
        LocalDate date = LocalDate.now();

        ActivityDate activity = ActivityDateMother.create(userId, date);

        assertNotNull(activity.id());
        assertEquals(userId, activity.userId());
        assertEquals(date, activity.activityDate());
    }

    @Test
    void shouldRegisterActivityRecordedEvent() {
        UserProfileId userId = UserProfileId.generate();
        LocalDate date = LocalDate.now();

        ActivityDate activity = ActivityDateMother.create(userId, date);

        var events = activity.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(ActivityRecordedEvent.class, events.get(0));

        ActivityRecordedEvent event = (ActivityRecordedEvent) events.get(0);
        assertEquals(userId, event.userId());
        assertEquals(date, event.date());
    }
}
