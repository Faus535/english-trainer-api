package com.faus535.englishtrainer.user.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

final class UserLevelTest {

    @ParameterizedTest
    @ValueSource(strings = {"a1", "a2", "b1", "b2", "c1", "c2"})
    void shouldCreateValidLevel(String level) {
        UserLevel userLevel = new UserLevel(level);

        assertEquals(level, userLevel.value());
    }

    @Test
    void shouldThrowWhenInvalidLevel() {
        assertThrows(IllegalArgumentException.class, () -> new UserLevel("x1"));
    }

    @Test
    void shouldThrowWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserLevel(null));
    }

    @Test
    void shouldReturnDefaultLevel() {
        UserLevel defaultLevel = UserLevel.defaultLevel();

        assertEquals("a1", defaultLevel.value());
    }

    @Test
    void shouldNormalizeToLowerCase() {
        UserLevel level = new UserLevel("B2");

        assertEquals("b2", level.value());
    }
}
