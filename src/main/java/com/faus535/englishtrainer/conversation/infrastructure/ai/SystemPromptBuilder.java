package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.ConversationLevel;

import java.util.Map;

class SystemPromptBuilder {

    private static final Map<String, String> ROLE_PLAY_SCENARIOS = Map.of(
            "job-interview", "Role: job interviewer. Ask about experience, skills, motivation.",
            "restaurant", "Role: waiter. Help order food, describe menu, handle requests.",
            "doctor-visit", "Role: doctor. Ask symptoms, give advice, explain treatments simply.",
            "travel", "Role: tourist info officer. Help plan trips, give directions, recommend places.",
            "hotel-checkin", "Role: hotel receptionist. Help check in, explain amenities, handle requests.",
            "shopping", "Role: shop assistant. Help find products, compare items, complete purchase."
    );

    static String build(ConversationLevel level, String topic, Float confidence) {
        return build(level, topic, confidence, false);
    }

    static String build(ConversationLevel level, String topic, Float confidence, boolean includeFeedback) {
        StringBuilder prompt = new StringBuilder();

        String scenario = topic != null ? ROLE_PLAY_SCENARIOS.get(topic.toLowerCase()) : null;
        if (scenario != null) {
            prompt.append("English practice scenario. ").append(scenario);
            prompt.append(" Stay in character + tutor. ");
        } else {
            prompt.append("Friendly English tutor. ");
            if (topic != null && !topic.isBlank()) {
                prompt.append("Topic: ").append(topic).append(". ");
            }
        }
        prompt.append("Student level: ").append(level.value().toUpperCase()).append(". ");

        appendLevelRules(prompt, level.value());
        appendConfidenceRules(prompt, confidence);

        if (includeFeedback) {
            appendFeedbackFormat(prompt);
        } else {
            prompt.append("Keep responses concise and natural. No feedback block needed.");
        }

        return prompt.toString();
    }

    static String buildSummaryPrompt() {
        return "Summarize this English tutoring session in 2-3 sentences: topics discussed, student strengths, areas to improve. Positive tone. No |||FEEDBACK||| block.";
    }

    private static void appendLevelRules(StringBuilder prompt, String level) {
        switch (level) {
            case "a1" -> prompt.append(
                    "A1 rules: very simple vocab, short sentences (5-8 words), present tense, yes/no questions, very tolerant of mistakes. ");
            case "a2" -> prompt.append(
                    "A2 rules: basic vocab, simple sentences (8-12 words), past tense + 'going to', correct only major errors, simple open questions. ");
            case "b1" -> prompt.append(
                    "B1 rules: moderate vocab (10-15 words), varied tenses + conditionals, gentle grammar corrections with brief explanations, open-ended questions, introduce idioms. ");
            case "b2" -> prompt.append(
                    "B2 rules: rich vocab + idioms, complex structures + passive/reported speech, precise corrections including nuance, debate + argumentation. ");
            case "c1", "c2" -> prompt.append(
                    "C1/C2 rules: sophisticated/academic language, correct all errors including style/register, complex abstract topics, focus on precision + natural phrasing. ");
        }
    }

    private static void appendConfidenceRules(StringBuilder prompt, Float confidence) {
        if (confidence == null) return;
        if (confidence < 0.5f) {
            prompt.append("STT confidence LOW (%.0f%%), ask to repeat before correcting. ".formatted(confidence * 100));
        } else if (confidence < 0.75f) {
            prompt.append("STT confidence moderate (%.0f%%), some words may be misrecognized. ".formatted(confidence * 100));
        }
    }

    private static void appendFeedbackFormat(StringBuilder prompt) {
        prompt.append("""
                After your response, include: \
                |||FEEDBACK|||{"grammarCorrections":[],"vocabularySuggestions":[],"pronunciationTips":[],"encouragement":""}|||END_FEEDBACK||| \
                with actual values. Must be valid JSON on its own line.""");
    }
}
