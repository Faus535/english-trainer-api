package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class AnthropicAiTutorAdapter implements AiTutorPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicAiTutorAdapter.class);
    private static final Pattern FEEDBACK_PATTERN =
            Pattern.compile("<<F>>(.*?)<<F>>", Pattern.DOTALL);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int FEEDBACK_EVERY_N_USER_TURNS = 5;
    private static final Map<String, AiTutorResponse> GREETING_CACHE = new ConcurrentHashMap<>();
    private static final int GREETING_MAX_TOKENS = 150;
    private static final int NO_FEEDBACK_MAX_TOKENS = 200;
    private static final int SUMMARY_MAX_TOKENS = 200;

    private final RestClient restClient;
    private final String model;
    private final int maxTokens;

    AnthropicAiTutorAdapter(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model,
            @Value("${anthropic.max-tokens:300}") int maxTokens) {
        this.model = model;
        this.maxTokens = maxTokens;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("anthropic-beta", "prompt-caching-2024-07-31")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public AiTutorResponse chat(ConversationLevel level, String topic,
                                 List<ConversationTurn> turns, Float confidence) throws AiTutorException {
        List<Map<String, String>> messages = buildMessages(turns);

        if (messages.isEmpty()) {
            String cacheKey = level.value() + ":" + (topic != null ? topic : "general");
            AiTutorResponse cached = GREETING_CACHE.get(cacheKey);
            if (cached != null) {
                return cached;
            }
            messages.add(Map.of("role", "user",
                    "content", "Start. Greet student, introduce topic."));
            String systemPrompt = SystemPromptBuilder.build(level, topic, confidence, false);
            String rawResponse = callClaude(systemPrompt, messages, GREETING_MAX_TOKENS);
            AiTutorResponse response = parseResponse(rawResponse);
            GREETING_CACHE.put(cacheKey, response);
            return response;
        }

        long userTurnCount = turns.stream().filter(t -> "user".equals(t.role())).count();
        boolean includeFeedback = userTurnCount % FEEDBACK_EVERY_N_USER_TURNS == 0;
        String systemPrompt = SystemPromptBuilder.build(level, topic, confidence, includeFeedback);
        int tokens = includeFeedback ? maxTokens : NO_FEEDBACK_MAX_TOKENS;
        String rawResponse = callClaude(systemPrompt, messages, tokens);
        return parseResponse(rawResponse);
    }

    @Override
    public String summarize(ConversationLevel level, List<ConversationTurn> turns) throws AiTutorException {
        String systemPrompt = SystemPromptBuilder.buildSummaryPrompt();
        StringBuilder conversationText = new StringBuilder("Conversation:\n");
        for (ConversationTurn turn : turns) {
            conversationText.append(turn.role().charAt(0)).append(":").append(turn.content()).append("\n");
        }

        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", conversationText.toString()));

        return callClaude(systemPrompt, messages, SUMMARY_MAX_TOKENS);
    }

    private String callClaude(String systemPrompt, List<Map<String, String>> messages, int tokensLimit) throws AiTutorException {
        List<Map<String, Object>> systemBlocks = List.of(
                Map.of("type", "text", "text", systemPrompt,
                        "cache_control", Map.of("type", "ephemeral"))
        );

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", tokensLimit,
                "system", systemBlocks,
                "messages", messages
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new AiTutorException("Empty response from Claude API");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            if (content == null || content.isEmpty()) {
                throw new AiTutorException("No content in Claude API response");
            }

            return (String) content.getFirst().get("text");
        } catch (AiTutorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Claude API call failed: {}", e.getMessage(), e);
            throw new AiTutorException("AI tutor service unavailable", e);
        }
    }

    private static final int RECENT_TURNS_FULL = 4;
    private static final int SUMMARY_THRESHOLD = 5;
    private static final int OLD_TURN_MAX_CHARS = 60;

    private List<Map<String, String>> buildMessages(List<ConversationTurn> turns) {
        List<Map<String, String>> messages = new ArrayList<>();
        int cutoff = Math.max(0, turns.size() - RECENT_TURNS_FULL);

        if (cutoff > 0 && turns.size() >= SUMMARY_THRESHOLD) {
            // Condense old turns into a single context summary (no API call needed)
            StringBuilder summary = new StringBuilder("[Prior context] ");
            for (int i = 0; i < cutoff; i++) {
                ConversationTurn turn = turns.get(i);
                String content = turn.content();
                if (content.length() > OLD_TURN_MAX_CHARS) {
                    content = content.substring(0, OLD_TURN_MAX_CHARS) + "...";
                }
                summary.append(turn.role().charAt(0)).append(":").append(content).append(" ");
            }
            messages.add(Map.of("role", "user", "content", summary.toString().trim()));
            messages.add(Map.of("role", "assistant", "content", "OK, I have the context. Let's continue."));
        } else {
            // Few turns: send them individually
            for (int i = 0; i < cutoff; i++) {
                ConversationTurn turn = turns.get(i);
                messages.add(Map.of("role", turn.role(), "content", turn.content()));
            }
        }

        // Recent turns: send in full
        for (int i = cutoff; i < turns.size(); i++) {
            ConversationTurn turn = turns.get(i);
            messages.add(Map.of("role", turn.role(), "content", turn.content()));
        }

        return messages;
    }

    @SuppressWarnings("unchecked")
    private AiTutorResponse parseResponse(String rawResponse) {
        Matcher matcher = FEEDBACK_PATTERN.matcher(rawResponse);
        if (matcher.find()) {
            String content = rawResponse.substring(0, matcher.start()).trim();
            String feedbackJson = matcher.group(1).trim();
            try {
                Map<String, Object> map = MAPPER.readValue(feedbackJson, Map.class);
                // Support both abbreviated (g,v,p,e) and full field names
                List<String> grammar = toStringList(map.getOrDefault("g", map.get("grammarCorrections")));
                List<String> vocab = toStringList(map.getOrDefault("v", map.get("vocabularySuggestions")));
                List<String> pronunciation = toStringList(map.getOrDefault("p", map.get("pronunciationTips")));
                String encouragement = (String) map.getOrDefault("e", map.get("encouragement"));
                TutorFeedback feedback = new TutorFeedback(grammar, vocab, pronunciation, encouragement);
                return new AiTutorResponse(content, feedback);
            } catch (Exception e) {
                log.warn("Failed to parse feedback JSON: {}", feedbackJson, e);
                return new AiTutorResponse(content, TutorFeedback.empty());
            }
        }
        return new AiTutorResponse(rawResponse, TutorFeedback.empty());
    }

    @SuppressWarnings("unchecked")
    private List<String> toStringList(Object obj) {
        if (obj instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }
}
