package com.faus535.englishtrainer.immerse.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmerseContentSizingTest {

    @Test
    void forLevel_a1_returnsSmallestSizing() {
        ImmerseContentSizing sizing = ImmerseContentSizing.forLevel("a1");
        assertEquals(800, sizing.generateMaxTokens());
        assertEquals(80, sizing.minWords());
        assertEquals(120, sizing.maxWords());
        assertEquals(3, sizing.exerciseCount());
        assertEquals(4, sizing.vocabCount());
        assertEquals(480, sizing.processingMaxTokens());
    }

    @Test
    void forLevel_b1_returnsMediumSizing() {
        ImmerseContentSizing sizing = ImmerseContentSizing.forLevel("b1");
        assertEquals(2000, sizing.generateMaxTokens());
        assertEquals(200, sizing.minWords());
        assertEquals(350, sizing.maxWords());
        assertEquals(5, sizing.exerciseCount());
        assertEquals(6, sizing.vocabCount());
    }

    @Test
    void forLevel_c2_returnsLargestSizing() {
        ImmerseContentSizing sizing = ImmerseContentSizing.forLevel("c2");
        assertEquals(3500, sizing.generateMaxTokens());
        assertEquals(500, sizing.minWords());
        assertEquals(700, sizing.maxWords());
        assertEquals(6, sizing.exerciseCount());
        assertEquals(10, sizing.vocabCount());
    }

    @Test
    void forLevel_null_defaultsToB1() {
        ImmerseContentSizing sizing = ImmerseContentSizing.forLevel(null);
        assertEquals(2000, sizing.generateMaxTokens());
        assertEquals(200, sizing.minWords());
    }

    @Test
    void forLevel_unknownLevel_defaultsToB1() {
        ImmerseContentSizing sizing = ImmerseContentSizing.forLevel("z9");
        assertEquals(2000, sizing.generateMaxTokens());
        assertEquals(200, sizing.minWords());
    }
}
