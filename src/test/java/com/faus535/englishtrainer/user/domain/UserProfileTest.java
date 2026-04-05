package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.event.UserProfileCreatedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class UserProfileTest {

    @Test
    void shouldCreateWithDefaultValues() {
        UserProfile profile = UserProfileMother.create();

        assertNotNull(profile.id());
        assertEquals(0, profile.xp());
        assertNotNull(profile.createdAt());
        assertNotNull(profile.updatedAt());
    }

    @Test
    void shouldAddXp() throws InvalidXpAmountException {
        UserProfile profile = UserProfileMother.create();

        UserProfile updated = profile.addXp(100);

        assertEquals(100, updated.xp());
        assertEquals(profile.id(), updated.id());
    }

    @Test
    void shouldThrowWhenNegativeXp() {
        UserProfile profile = UserProfileMother.create();

        assertThrows(InvalidXpAmountException.class, () -> profile.addXp(-10));
    }

    @Test
    void shouldRegisterCreatedEvent() {
        UserProfile profile = UserProfileMother.create();

        var events = profile.pullDomainEvents();

        assertEquals(1, events.size());
        assertInstanceOf(UserProfileCreatedEvent.class, events.getFirst());
        UserProfileCreatedEvent event = (UserProfileCreatedEvent) events.getFirst();
        assertEquals(profile.id(), event.profileId());
    }
}
