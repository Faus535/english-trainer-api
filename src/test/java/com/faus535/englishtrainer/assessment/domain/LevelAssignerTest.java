package com.faus535.englishtrainer.assessment.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LevelAssignerTest {

    @Test
    void shouldAssignC1WhenScoreIs85OrAbove() {
        assertEquals("c1", LevelAssigner.assignLevel(85));
        assertEquals("c1", LevelAssigner.assignLevel(100));
        assertEquals("c1", LevelAssigner.assignLevel(90));
    }

    @Test
    void shouldAssignB2WhenScoreBetween70And84() {
        assertEquals("b2", LevelAssigner.assignLevel(70));
        assertEquals("b2", LevelAssigner.assignLevel(84));
        assertEquals("b2", LevelAssigner.assignLevel(75));
    }

    @Test
    void shouldAssignB1WhenScoreBetween55And69() {
        assertEquals("b1", LevelAssigner.assignLevel(55));
        assertEquals("b1", LevelAssigner.assignLevel(69));
        assertEquals("b1", LevelAssigner.assignLevel(60));
    }

    @Test
    void shouldAssignA2WhenScoreBetween40And54() {
        assertEquals("a2", LevelAssigner.assignLevel(40));
        assertEquals("a2", LevelAssigner.assignLevel(54));
        assertEquals("a2", LevelAssigner.assignLevel(45));
    }

    @Test
    void shouldAssignA1WhenScoreBelow40() {
        assertEquals("a1", LevelAssigner.assignLevel(39));
        assertEquals("a1", LevelAssigner.assignLevel(0));
        assertEquals("a1", LevelAssigner.assignLevel(20));
    }
}
