package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.ConversationLevel;

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
        String cacheKey = level.value() + ":" + (topic != null ? topic : "") + ":" + includeFeedback
                + ":" + confidenceBucket(confidence);
        return PROMPT_CACHE.computeIfAbsent(cacheKey, k -> buildPrompt(level, topic, confidence, includeFeedback));
    }

    private static String buildPrompt(ConversationLevel level, String topic, Float confidence, boolean includeFeedback) {
        StringBuilder p = new StringBuilder();

        String scenario = topic != null ? ROLE_PLAY_SCENARIOS.get(topic.toLowerCase()) : null;
        if (scenario != null) {
            p.append("EN tutor+").append(scenario).append(" Stay in character. ");
        } else {
            p.append("EN tutor. ");
            if (topic != null && !topic.isBlank()) {
                p.append("Topic:").append(topic).append(". ");
            }
        }
        p.append("Lvl:").append(level.value().toUpperCase()).append(". ");

        appendLevelRules(p, level.value());
        appendConfidenceRules(p, confidence);

        if (includeFeedback) {
            appendFeedbackFormat(p);
        } else {
            p.append("Concise,natural. No feedback block.");
        }

        return p.toString();
    }

    static String buildSummaryPrompt() {
        return "Summarize tutoring session in 2-3 sentences: topics,strengths,improvements. Positive. No feedback block.";
    }

    private static void appendLevelRules(StringBuilder p, String level) {
        switch (level) {
            case "a1" -> p.append("A1:simple vocab,5-8w sentences,present tense,yes/no Qs,tolerant. Max 2 sent. ");
            case "a2" -> p.append("A2:basic vocab,8-12w,past+'going to',fix major errors,simple Qs. Max 2 sent. ");
            case "b1" -> p.append("B1:moderate vocab,10-15w,varied tenses+conditionals,gentle fixes+brief explain,open Qs,idioms. Max 3 sent. ");
            case "b2" -> p.append("B2:rich vocab+idioms,complex structures+passive/reported,precise fixes+nuance,debate. Max 3 sent. ");
            case "c1", "c2" -> p.append("C1/C2:sophisticated/academic,fix all errors+style/register,abstract topics,precision+natural. Max 4 sent. ");
        }
    }

    private static void appendConfidenceRules(StringBuilder p, Float confidence) {
        if (confidence == null) return;
        if (confidence < 0.5f) {
            p.append("STT LOW(%.0f%%),ask repeat. ".formatted(confidence * 100));
        } else if (confidence < 0.75f) {
            p.append("STT mid(%.0f%%),possible misrecognition. ".formatted(confidence * 100));
        }
    }

    private static void appendFeedbackFormat(StringBuilder p) {
        p.append("After reply add: <<F>>{\"g\":[],\"v\":[],\"p\":[],\"e\":\"\"}<<F>> with values. Valid JSON.");
    }

    private static String confidenceBucket(Float confidence) {
        if (confidence == null) return "null";
        if (confidence < 0.5f) return "low";
        if (confidence < 0.75f) return "mid";
        return "high";
    }
}
