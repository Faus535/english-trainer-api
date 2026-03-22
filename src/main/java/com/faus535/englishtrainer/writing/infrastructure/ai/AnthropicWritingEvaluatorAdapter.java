package com.faus535.englishtrainer.writing.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.writing.domain.WritingEvaluatorPort;
import com.faus535.englishtrainer.writing.domain.WritingFeedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
class AnthropicWritingEvaluatorAdapter implements WritingEvaluatorPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicWritingEvaluatorAdapter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RestClient restClient;
    private final String model;
    private final int maxTokens;

    AnthropicWritingEvaluatorAdapter(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.writing-model:claude-haiku-4-5-20251001}") String model,
            @Value("${anthropic.writing-max-tokens:600}") int maxTokens) {
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
    public WritingFeedback evaluate(String text, String exercisePrompt, String level) throws Exception {
        String systemPrompt = """
                English writing evaluator. Student CEFR level: %s.
                Respond ONLY with JSON:
                {"grammarScore":0-100,"coherenceScore":0-100,"vocabularyScore":0-100,"overallScore":0-100,"levelAssessment":"a1/a2/b1/b2/c1/c2","generalFeedback":"brief feedback","corrections":["correction 1"]}
                """.formatted(level);

        String userMessage = "Prompt: " + exercisePrompt + "\n\nStudent's text:\n" + text;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", userMessage))
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) throw new Exception("Empty response from Claude API");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        String rawText = (String) content.getFirst().get("text");

        // Extract JSON from response
        int jsonStart = rawText.indexOf('{');
        int jsonEnd = rawText.lastIndexOf('}') + 1;
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            String json = rawText.substring(jsonStart, jsonEnd);
            return MAPPER.readValue(json, WritingFeedback.class);
        }

        log.warn("Could not parse writing feedback from response: {}", rawText);
        return WritingFeedback.empty();
    }
}
