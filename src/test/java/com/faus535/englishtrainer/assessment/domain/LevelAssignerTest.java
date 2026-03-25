package com.faus535.englishtrainer.assessment.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LevelAssignerTest {

    @Test
    void shouldAssignC2WhenScoreIs95OrAbove() {
        assertEquals("c2", LevelAssigner.assignLevel(95));
        assertEquals("c2", LevelAssigner.assignLevel(100));
        assertEquals("c2", LevelAssigner.assignLevel(98));
    }

    @Test
    void shouldAssignC1WhenScoreBetween80And94() {
        assertEquals("c1", LevelAssigner.assignLevel(80));
        assertEquals("c1", LevelAssigner.assignLevel(94));
        assertEquals("c1", LevelAssigner.assignLevel(85));
    }

    @Test
    void shouldAssignB2WhenScoreBetween65And79() {
        assertEquals("b2", LevelAssigner.assignLevel(65));
        assertEquals("b2", LevelAssigner.assignLevel(79));
        assertEquals("b2", LevelAssigner.assignLevel(70));
    }

    @Test
    void shouldAssignB1WhenScoreBetween50And64() {
        assertEquals("b1", LevelAssigner.assignLevel(50));
        assertEquals("b1", LevelAssigner.assignLevel(64));
        assertEquals("b1", LevelAssigner.assignLevel(55));
    }

    @Test
    void shouldAssignA2WhenScoreBetween35And49() {
        assertEquals("a2", LevelAssigner.assignLevel(35));
        assertEquals("a2", LevelAssigner.assignLevel(49));
        assertEquals("a2", LevelAssigner.assignLevel(40));
    }

    @Test
    void shouldAssignA1WhenScoreBelow35() {
        assertEquals("a1", LevelAssigner.assignLevel(34));
        assertEquals("a1", LevelAssigner.assignLevel(0));
        assertEquals("a1", LevelAssigner.assignLevel(20));
    }
}
