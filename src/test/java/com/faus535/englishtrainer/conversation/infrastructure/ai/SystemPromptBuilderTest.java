package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.ConversationLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemPromptBuilderTest {

    @Test
    void shouldIncludeLevelInPrompt() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, null);

        assertTrue(prompt.contains("B1"));
        assertTrue(prompt.contains("Intermediate"));
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
        assertTrue(prompt.contains("clarify"));
    }

    @Test
    void shouldIncludeModerateConfidenceNote() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), null, 0.6f);

        assertTrue(prompt.contains("MODERATE"));
    }

    @Test
    void shouldIncludeFeedbackFormat() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null);

        assertTrue(prompt.contains("|||FEEDBACK|||"));
        assertTrue(prompt.contains("|||END_FEEDBACK|||"));
    }

    @Test
    void shouldBuildA1Rules() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("a1"), null, null);

        assertTrue(prompt.contains("Beginner"));
        assertTrue(prompt.contains("simple vocabulary"));
    }

    @Test
    void shouldBuildC1Rules() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("c1"), null, null);

        assertTrue(prompt.contains("Advanced"));
        assertTrue(prompt.contains("sophisticated"));
    }

    @Test
    void shouldBuildRolePlayPrompt() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), "job-interview", null);

        assertTrue(prompt.contains("role"));
        assertTrue(prompt.contains("interviewer"));
    }

    @Test
    void shouldFallbackToTopicWhenNotRolePlay() {
        String prompt = SystemPromptBuilder.build(new ConversationLevel("b1"), "Cooking", null);

        assertTrue(prompt.contains("Cooking"));
        assertFalse(prompt.contains("role"));
    }

    @Test
    void shouldBuildSummaryPrompt() {
        String prompt = SystemPromptBuilder.buildSummaryPrompt();

        assertTrue(prompt.contains("summarizing"));
        assertTrue(prompt.contains("Key strengths"));
    }
}
