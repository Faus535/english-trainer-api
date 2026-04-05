package com.faus535.englishtrainer.talk.infrastructure.ai;

import com.faus535.englishtrainer.talk.domain.LevelProfile;
import com.faus535.englishtrainer.talk.domain.LevelProfiles;
import com.faus535.englishtrainer.talk.domain.TalkLevel;
import com.faus535.englishtrainer.talk.domain.TalkScenario;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TalkSystemPromptBuilder {

    private static final Map<String, String> PROMPT_CACHE = new ConcurrentHashMap<>();

    static String build(TalkLevel level, TalkScenario scenario, Float confidence, boolean includeFeedback) {
        String scenarioKey = scenario != null ? scenario.id().value().toString() : "none";
        String cacheKey = level.value() + ":" + scenarioKey + ":" + includeFeedback + ":" + confidenceBucket(confidence);
        return PROMPT_CACHE.computeIfAbsent(cacheKey,
                k -> buildPrompt(level, scenario, confidence, includeFeedback));
    }

    private static String buildPrompt(TalkLevel level, TalkScenario scenario, Float confidence,
                                       boolean includeFeedback) {
        StringBuilder p = new StringBuilder();

        if (scenario != null) {
            p.append("You are playing a role in a conversation scenario.\n");
            p.append("Scenario: ").append(scenario.title()).append("\n");
            p.append("Your role: ").append(scenario.contextPrompt()).append("\n\n");
        } else {
            p.append("You are an English tutor having a natural conversation with a student.\n\n");
        }

        appendLevelRules(p, level.value());
        appendConfidenceRules(p, confidence);

        if (includeFeedback) {
            appendFeedbackFormat(p, level.value());
        } else {
            p.append("Be concise and natural. Do not include any feedback block.\n");
        }

        return p.toString();
    }

    static String buildSummaryPrompt() {
        return "Summarize tutoring session in 2-3 sentences: topics,strengths,improvements. Positive. No feedback block.";
    }

    private static void appendLevelRules(StringBuilder p, String level) {
        LevelProfile profile = LevelProfiles.forLevel(level);

        p.append("=== STUDENT LEVEL: ").append(level.toUpperCase()).append(" ===\n\n");

        p.append("SENTENCE RULES:\n");
        p.append("- Your sentences must be ").append(profile.sentenceLength()).append(" long.\n");
        p.append("- Maximum ").append(profile.maxSentences()).append(" sentence(s) per turn.\n\n");

        p.append("ALLOWED TENSES:\n");
        for (String tense : profile.allowedTenses()) {
            p.append("- ").append(tense).append("\n");
        }
        p.append("- Do NOT use any tense not listed above.\n\n");

        p.append("VOCABULARY:\n");
        p.append("- ").append(profile.vocabularyBand()).append("\n\n");

        p.append("ERROR CORRECTION:\n");
        p.append("- ").append(profile.errorCorrectionStyle()).append("\n\n");

        p.append("QUESTION STYLE:\n");
        p.append("- ").append(profile.questionStyle()).append("\n\n");
    }

    private static void appendConfidenceRules(StringBuilder p, Float confidence) {
        if (confidence == null) return;
        if (confidence < 0.5f) {
            p.append("SPEECH RECOGNITION: Low confidence (%.0f%%). Ask them to repeat.\n\n".formatted(confidence * 100));
        } else if (confidence < 0.75f) {
            p.append("SPEECH RECOGNITION: Medium confidence (%.0f%%). Clarify if meaning unclear.\n\n".formatted(confidence * 100));
        }
    }

    private static void appendFeedbackFormat(StringBuilder p, String level) {
        p.append("After your reply, add a feedback block in this exact format:\n");
        p.append("<<F>>{");

        switch (level) {
            case "a1", "a2" -> p.append("\"e\":\"encouragement message\"");
            case "b1" -> p.append("\"g\":[\"max 1 grammar correction\"],\"e\":\"encouragement\"");
            case "b2" -> p.append("\"g\":[\"grammar corrections\"],\"v\":[\"vocabulary suggestions\"],\"e\":\"encouragement\"");
            default -> p.append("\"g\":[\"grammar corrections\"],\"v\":[\"vocabulary suggestions\"],\"p\":[\"pronunciation/style tips\"],\"e\":\"encouragement\"");
        }

        p.append("}<<F>>\n");
        p.append("Use valid JSON. Include only the categories listed above for this level.\n");
    }

    private static String confidenceBucket(Float confidence) {
        if (confidence == null) return "null";
        if (confidence < 0.5f) return "low";
        if (confidence < 0.75f) return "mid";
        return "high";
    }
}
