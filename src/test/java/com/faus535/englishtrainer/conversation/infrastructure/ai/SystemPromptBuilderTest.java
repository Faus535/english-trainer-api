package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.ConversationLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemPromptBuilderTest {

    @Test
    void shouldIncludeLevelInPrompt() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, null);

        assertTrue(prompt.contains("B1"));
        assertTrue(prompt.contains("STUDENT LEVEL: B1"));
    }

    @Test
    void shouldIncludeTopicWhenProvided() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), "Food", null);

        assertTrue(prompt.contains("Food"));
    }

    @Test
    void shouldIncludeLowConfidenceWarning() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, 0.3f);

        assertTrue(prompt.contains("Low confidence"));
        assertTrue(prompt.contains("repeat"));
    }

    @Test
    void shouldIncludeModerateConfidenceNote() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, 0.6f);

        assertTrue(prompt.contains("Medium confidence"));
    }

    @Test
    void shouldIncludeFeedbackFormatWhenRequested() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null, true);

        assertTrue(prompt.contains("<<F>>"));
    }

    @Test
    void shouldNotIncludeFeedbackByDefault() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null);

        assertFalse(prompt.contains("<<F>>"));
    }

    @Test
    void shouldBuildA1RulesWithPresentTenseOnly() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null);

        assertTrue(prompt.contains("STUDENT LEVEL: A1"));
        assertTrue(prompt.contains("present simple ONLY"));
        assertTrue(prompt.contains("top 300"));
        assertTrue(prompt.contains("FORBIDDEN TOPICS"));
    }

    @Test
    void shouldBuildB1RulesWithPhrasalVerbsAndOpinions() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, null);

        assertTrue(prompt.contains("phrasal verb"));
        assertTrue(prompt.contains("opinions"));
    }

    @Test
    void shouldBuildC1RulesWithStyleCorrection() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("c1"), null, null);

        assertTrue(prompt.contains("STUDENT LEVEL: C1"));
        assertTrue(prompt.contains("style"));
        assertTrue(prompt.contains("register"));
    }

    @Test
    void shouldDifferentiateA1AndB1InMultipleDimensions() {
        String a1 = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null);
        String b1 = SystemPromptBuilder.build(new ConversationLevel("b1"), null, null);

        // Different tenses
        assertTrue(a1.contains("present simple ONLY"));
        assertTrue(b1.contains("present perfect"));

        // Different vocabulary bands
        assertTrue(a1.contains("top 300"));
        assertTrue(b1.contains("top 1500"));

        // Different max sentences
        assertTrue(a1.contains("Maximum 1 sentence"));
        assertTrue(b1.contains("Maximum 2 sentence"));

        // Different error correction
        assertTrue(a1.contains("Ignore ALL errors"));
        assertTrue(b1.contains("Gentle correction"));
    }

    @Test
    void shouldBuildRolePlayPrompt() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), "job-interview", null);

        assertTrue(prompt.contains("roleplay"));
        assertTrue(prompt.contains("interviewer"));
    }

    @Test
    void shouldFallbackToTopicWhenNotRolePlay() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), "Cooking", null);

        assertTrue(prompt.contains("Cooking"));
    }

    @Test
    void shouldBuildSummaryPrompt() {
        String prompt = SystemPromptBuilder.buildSummaryPrompt();

        assertTrue(prompt.contains("Summarize"));
        assertTrue(prompt.contains("strengths"));
    }

    @Test
    void shouldLimitFeedbackCategoriesForA1() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null, true);

        assertTrue(prompt.contains("\"e\":\"encouragement message\""));
        assertFalse(prompt.contains("\"g\":"));
    }

    @Test
    void shouldIncludeFullFeedbackForC1() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("c1"), null, null, true);

        assertTrue(prompt.contains("\"g\":"));
        assertTrue(prompt.contains("\"v\":"));
        assertTrue(prompt.contains("\"p\":"));
        assertTrue(prompt.contains("\"e\":"));
    }
}
