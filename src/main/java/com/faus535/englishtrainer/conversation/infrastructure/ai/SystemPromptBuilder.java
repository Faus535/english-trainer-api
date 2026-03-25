package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.ConversationGoal;
import com.faus535.englishtrainer.conversation.domain.ConversationLevel;
import com.faus535.englishtrainer.conversation.domain.LevelProfile;
import com.faus535.englishtrainer.conversation.domain.LevelProfiles;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SystemPromptBuilder {

    private static final Map<String, String> ROLE_PLAY_SCENARIOS = Map.of(
            "job-interview", "Roleplay:interviewer. Ask experience,skills,motivation.",
            "restaurant", "Roleplay:waiter. Menu,orders,requests.",
            "doctor-visit", "Roleplay:doctor. Symptoms,advice,treatments.",
            "travel", "Roleplay:tourist-info. Trips,directions,places.",
            "hotel-checkin", "Roleplay:receptionist. Checkin,amenities,requests.",
            "shopping", "Roleplay:shop-assistant. Products,compare,purchase."
    );

    private static final Map<String, String> PROMPT_CACHE = new ConcurrentHashMap<>();

    static String build(ConversationLevel level, String topic, Float confidence) {
        return build(level, topic, confidence, false);
    }

    static String build(ConversationLevel level, String topic, Float confidence, boolean includeFeedback) {
        return build(level, topic, confidence, includeFeedback, List.of());
    }

    static String build(ConversationLevel level, String topic, Float confidence, boolean includeFeedback,
                         List<ConversationGoal> goals) {
        String goalsKey = goals != null && !goals.isEmpty() ? String.valueOf(goals.hashCode()) : "";
        String cacheKey = level.value() + ":" + (topic != null ? topic : "") + ":" + includeFeedback
                + ":" + confidenceBucket(confidence) + ":" + goalsKey;
        return PROMPT_CACHE.computeIfAbsent(cacheKey,
                k -> buildPrompt(level, topic, confidence, includeFeedback, goals));
    }

    private static String buildPrompt(ConversationLevel level, String topic, Float confidence,
                                       boolean includeFeedback, List<ConversationGoal> goals) {
        StringBuilder p = new StringBuilder();

        String scenario = topic != null ? ROLE_PLAY_SCENARIOS.get(topic.toLowerCase()) : null;
        if (scenario != null) {
            p.append("You are an English tutor in a roleplay scenario. ").append(scenario).append(" Stay in character.\n\n");
        } else {
            p.append("You are an English tutor having a conversation with a student.\n");
            if (topic != null && !topic.isBlank()) {
                p.append("Topic: ").append(topic).append(".\n");
            }
            p.append("\n");
        }

        appendLevelRules(p, level.value());
        appendConfidenceRules(p, confidence);
        appendGoals(p, goals);

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

        p.append("TOPICS TO DISCUSS:\n");
        for (String topic : profile.topicExamples()) {
            p.append("- ").append(topic).append("\n");
        }
        p.append("\n");

        if (!profile.forbiddenTopics().isEmpty()) {
            p.append("FORBIDDEN TOPICS (never bring these up):\n");
            for (String forbidden : profile.forbiddenTopics()) {
                p.append("- ").append(forbidden).append("\n");
            }
            p.append("\n");
        }

        p.append("VOCABULARY:\n");
        p.append("- ").append(profile.vocabularyBand()).append("\n\n");

        p.append("ERROR CORRECTION:\n");
        p.append("- ").append(profile.errorCorrectionStyle()).append("\n\n");

        p.append("QUESTION STYLE:\n");
        p.append("- ").append(profile.questionStyle()).append("\n\n");

        p.append("CONVERSATION GOAL:\n");
        p.append("- ").append(profile.conversationGoal()).append("\n\n");
    }

    private static void appendConfidenceRules(StringBuilder p, Float confidence) {
        if (confidence == null) return;
        if (confidence < 0.5f) {
            p.append("SPEECH RECOGNITION: Low confidence (%.0f%%). The student's audio was unclear. Ask them to repeat.\n\n".formatted(confidence * 100));
        } else if (confidence < 0.75f) {
            p.append("SPEECH RECOGNITION: Medium confidence (%.0f%%). Possible misrecognition. Proceed but clarify if meaning is unclear.\n\n".formatted(confidence * 100));
        }
    }

    private static void appendGoals(StringBuilder p, List<ConversationGoal> goals) {
        if (goals == null || goals.isEmpty()) return;
        p.append("CONVERSATION GOALS:\n");
        for (int i = 0; i < goals.size(); i++) {
            ConversationGoal goal = goals.get(i);
            p.append(i + 1).append(") ").append(goal.description());
            if (!goal.targetItems().isEmpty()) {
                p.append(" (target: ").append(String.join(", ", goal.targetItems())).append(")");
            }
            p.append("\n");
        }
        p.append("Guide the conversation toward these goals.\n\n");
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
