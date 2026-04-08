package com.faus535.englishtrainer.article.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleContentSizingTest {

    @Test
    void shouldReturnB1SizingWhenLevelIsB1() {
        ArticleContentSizing sizing = ArticleContentSizing.forLevel("b1");
        assertEquals(1400, sizing.generateMaxTokens());
        assertEquals(200, sizing.translateMaxTokens());
        assertEquals(1050, sizing.questionsMaxTokens());
        assertEquals(420, sizing.correctAnswerMaxTokens());
    }

    @Test
    void shouldReturnB2SizingWhenLevelIsB2() {
        ArticleContentSizing sizing = ArticleContentSizing.forLevel("b2");
        assertEquals(1600, sizing.generateMaxTokens());
        assertEquals(200, sizing.translateMaxTokens());
        assertEquals(1200, sizing.questionsMaxTokens());
        assertEquals(480, sizing.correctAnswerMaxTokens());
    }

    @Test
    void shouldReturnC1SizingWhenLevelIsC1() {
        ArticleContentSizing sizing = ArticleContentSizing.forLevel("c1");
        assertEquals(1800, sizing.generateMaxTokens());
        assertEquals(200, sizing.translateMaxTokens());
        assertEquals(1350, sizing.questionsMaxTokens());
        assertEquals(540, sizing.correctAnswerMaxTokens());
    }

    @Test
    void shouldDefaultToB1WhenLevelIsNull() {
        ArticleContentSizing sizing = ArticleContentSizing.forLevel(null);
        assertEquals(1400, sizing.generateMaxTokens());
    }

    @Test
    void shouldDefaultToB1WhenLevelIsUnknown() {
        assertEquals(1400, ArticleContentSizing.forLevel("a1").generateMaxTokens());
        assertEquals(1400, ArticleContentSizing.forLevel("z9").generateMaxTokens());
    }

    @Test
    void shouldNormalizeMixedCaseAndWhitespace() {
        assertEquals(1400, ArticleContentSizing.forLevel("B1").generateMaxTokens());
        assertEquals(1400, ArticleContentSizing.forLevel(" b1 ").generateMaxTokens());
        assertEquals(1600, ArticleContentSizing.forLevel("B2").generateMaxTokens());
    }
}
