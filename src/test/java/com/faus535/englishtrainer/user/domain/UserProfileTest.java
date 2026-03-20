package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.event.UserProfileCreatedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class UserProfileTest {

    @Test
    void shouldCreateWithDefaultValues() {
        UserProfile profile = UserProfileMother.create();

        assertNotNull(profile.id());
        assertFalse(profile.testCompleted());
        assertEquals(UserLevel.defaultLevel(), profile.levelListening());
        assertEquals(UserLevel.defaultLevel(), profile.levelVocabulary());
        assertEquals(UserLevel.defaultLevel(), profile.levelGrammar());
        assertEquals(UserLevel.defaultLevel(), profile.levelPhrases());
        assertEquals(UserLevel.defaultLevel(), profile.levelPronunciation());
        assertEquals(0, profile.sessionCount());
        assertEquals(0, profile.sessionsThisWeek());
        assertNull(profile.weekStart());
        assertEquals(0, profile.xp());
        assertNotNull(profile.createdAt());
        assertNotNull(profile.updatedAt());
    }

    @Test
    void shouldMarkTestCompleted() {
        UserProfile profile = UserProfileMother.create();

        UserProfile updated = profile.markTestCompleted();

        assertTrue(updated.testCompleted());
        assertEquals(profile.id(), updated.id());
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
    void shouldUpdateModuleLevelListening() throws InvalidModuleException {
        UserProfile profile = UserProfileMother.create();
        UserLevel b2 = new UserLevel("b2");

        UserProfile updated = profile.updateModuleLevel("listening", b2);

        assertEquals(b2, updated.levelListening());
    }

    @Test
    void shouldUpdateModuleLevelVocabulary() throws InvalidModuleException {
        UserProfile profile = UserProfileMother.create();
        UserLevel b1 = new UserLevel("b1");

        UserProfile updated = profile.updateModuleLevel("vocabulary", b1);

        assertEquals(b1, updated.levelVocabulary());
    }

    @Test
    void shouldUpdateModuleLevelGrammar() throws InvalidModuleException {
        UserProfile profile = UserProfileMother.create();
        UserLevel c1 = new UserLevel("c1");

        UserProfile updated = profile.updateModuleLevel("grammar", c1);

        assertEquals(c1, updated.levelGrammar());
    }

    @Test
    void shouldUpdateModuleLevelPhrases() throws InvalidModuleException {
        UserProfile profile = UserProfileMother.create();
        UserLevel a2 = new UserLevel("a2");

        UserProfile updated = profile.updateModuleLevel("phrases", a2);

        assertEquals(a2, updated.levelPhrases());
    }

    @Test
    void shouldUpdateModuleLevelPronunciation() throws InvalidModuleException {
        UserProfile profile = UserProfileMother.create();
        UserLevel c2 = new UserLevel("c2");

        UserProfile updated = profile.updateModuleLevel("pronunciation", c2);

        assertEquals(c2, updated.levelPronunciation());
    }

    @Test
    void shouldThrowWhenInvalidModule() {
        UserProfile profile = UserProfileMother.create();
        UserLevel level = new UserLevel("b1");

        assertThrows(InvalidModuleException.class, () -> profile.updateModuleLevel("invalid_module", level));
    }

    @Test
    void shouldRecordSession() {
        UserProfile profile = UserProfileMother.create();

        UserProfile updated = profile.recordSession();

        assertEquals(1, updated.sessionCount());
        assertEquals(1, updated.sessionsThisWeek());
    }

    @Test
    void shouldResetWeeklyCounters() {
        UserProfile profile = UserProfileMother.create().recordSession().recordSession();

        UserProfile updated = profile.resetWeeklyCounters();

        assertEquals(0, updated.sessionsThisWeek());
        assertEquals(2, updated.sessionCount());
        assertNotNull(updated.weekStart());
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
