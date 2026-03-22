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
            Pattern.compile("\\|\\|\\|FEEDBACK\\|\\|\\|(.*?)\\|\\|\\|END_FEEDBACK\\|\\|\\|", Pattern.DOTALL);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int FEEDBACK_EVERY_N_USER_TURNS = 3;
    private static final Map<String, AiTutorResponse> GREETING_CACHE = new ConcurrentHashMap<>();

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
                    "content", "Start the conversation. Greet the student and introduce the topic."));
            String systemPrompt = SystemPromptBuilder.build(level, topic, confidence, false);
            String rawResponse = callClaude(systemPrompt, messages);
            AiTutorResponse response = parseResponse(rawResponse);
            GREETING_CACHE.put(cacheKey, response);
            return response;
        }

        long userTurnCount = turns.stream().filter(t -> "user".equals(t.role())).count();
        boolean includeFeedback = userTurnCount % FEEDBACK_EVERY_N_USER_TURNS == 0;
        String systemPrompt = SystemPromptBuilder.build(level, topic, confidence, includeFeedback);
        String rawResponse = callClaude(systemPrompt, messages);
        return parseResponse(rawResponse);
    }

    @Override
    public String summarize(ConversationLevel level, List<ConversationTurn> turns) throws AiTutorException {
        String systemPrompt = SystemPromptBuilder.buildSummaryPrompt();
        StringBuilder conversationText = new StringBuilder("Here is the conversation to summarize:\n\n");
        for (ConversationTurn turn : turns) {
            conversationText.append(turn.role().toUpperCase()).append(": ").append(turn.content()).append("\n");
        }

        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", conversationText.toString()));

        return callClaude(systemPrompt, messages);
    }

    private String callClaude(String systemPrompt, List<Map<String, String>> messages) throws AiTutorException {
        List<Map<String, Object>> systemBlocks = List.of(
                Map.of("type", "text", "text", systemPrompt,
                        "cache_control", Map.of("type", "ephemeral"))
        );

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
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

    private static final int RECENT_TURNS_FULL = 6;
    private static final int OLD_MESSAGE_MAX_CHARS = 150;

    private List<Map<String, String>> buildMessages(List<ConversationTurn> turns) {
        List<Map<String, String>> messages = new ArrayList<>();
        int cutoff = Math.max(0, turns.size() - RECENT_TURNS_FULL);
        for (int i = 0; i < turns.size(); i++) {
            ConversationTurn turn = turns.get(i);
            String content = turn.content();
            if (i < cutoff && content.length() > OLD_MESSAGE_MAX_CHARS) {
                content = content.substring(0, OLD_MESSAGE_MAX_CHARS) + "...";
            }
            messages.add(Map.of("role", turn.role(), "content", content));
        }
        return messages;
    }

    private AiTutorResponse parseResponse(String rawResponse) {
        Matcher matcher = FEEDBACK_PATTERN.matcher(rawResponse);
        if (matcher.find()) {
            String content = rawResponse.substring(0, matcher.start()).trim();
            String feedbackJson = matcher.group(1).trim();
            try {
                TutorFeedback feedback = MAPPER.readValue(feedbackJson, TutorFeedback.class);
                return new AiTutorResponse(content, feedback);
            } catch (Exception e) {
                log.warn("Failed to parse feedback JSON: {}", feedbackJson, e);
                return new AiTutorResponse(content, TutorFeedback.empty());
            }
        }
        return new AiTutorResponse(rawResponse, TutorFeedback.empty());
    }
}
