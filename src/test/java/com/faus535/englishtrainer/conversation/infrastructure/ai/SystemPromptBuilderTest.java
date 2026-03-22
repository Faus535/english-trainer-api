package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.ConversationLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemPromptBuilderTest {

    @Test
    void shouldIncludeLevelInPrompt() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, null);

        assertTrue(prompt.contains("B1"));
        assertTrue(prompt.contains("B1:"));
    }

    @Test
    void shouldIncludeTopicWhenProvided() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), "Food", null);

        assertTrue(prompt.contains("Food"));
    }

    @Test
    void shouldIncludeLowConfidenceWarning() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, 0.3f);

        assertTrue(prompt.contains("LOW"));
        assertTrue(prompt.contains("repeat"));
    }

    @Test
    void shouldIncludeModerateConfidenceNote() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, 0.6f);

        assertTrue(prompt.contains("mid"));
    }

    @Test
    void shouldIncludeFeedbackFormatWhenRequested() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null, true);

        assertTrue(prompt.contains("<<F>>"));
    }

    @Test
    void shouldNotIncludeFeedbackByDefault() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null);

        assertFalse(prompt.contains("|||FEEDBACK|||"));
    }

    @Test
    void shouldBuildA1Rules() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null);

        assertTrue(prompt.contains("A1:"));
        assertTrue(prompt.contains("simple vocab"));
    }

    @Test
    void shouldBuildC1Rules() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("c1"), null, null);

        assertTrue(prompt.contains("C1/C2:"));
        assertTrue(prompt.contains("sophisticated"));
    }

    @Test
    void shouldBuildRolePlayPrompt() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), "job-interview", null);

        assertTrue(prompt.contains("Roleplay"));
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
}
