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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class AnthropicAiTutorAdapter implements AiTutorPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicAiTutorAdapter.class);
    private static final Pattern FEEDBACK_PATTERN =
            Pattern.compile("\\|\\|\\|FEEDBACK\\|\\|\\|(.*?)\\|\\|\\|END_FEEDBACK\\|\\|\\|", Pattern.DOTALL);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RestClient restClient;
    private final String model;
    private final int maxTokens;

    AnthropicAiTutorAdapter(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model:claude-sonnet-4-20250514}") String model,
            @Value("${anthropic.max-tokens:500}") int maxTokens) {
        this.model = model;
        this.maxTokens = maxTokens;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public AiTutorResponse chat(ConversationLevel level, String topic,
                                 List<ConversationTurn> turns, Float confidence) throws AiTutorException {
        String systemPrompt = SystemPromptBuilder.build(level, topic, confidence);
        List<Map<String, String>> messages = buildMessages(turns);

        if (messages.isEmpty()) {
            messages.add(Map.of("role", "user",
                    "content", "Start the conversation. Greet the student and introduce the topic."));
        }

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
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemPrompt,
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

    private List<Map<String, String>> buildMessages(List<ConversationTurn> turns) {
        List<Map<String, String>> messages = new ArrayList<>();
        for (ConversationTurn turn : turns) {
            messages.add(Map.of("role", turn.role(), "content", turn.content()));
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
