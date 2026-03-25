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

    private static final Map<String, Object> EVALUATE_TOOL = Map.of(
            "name", "evaluate_writing",
            "description", "Evaluate a student's English writing exercise",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "grammarScore", Map.of("type", "number", "description", "Grammar score 0-100"),
                            "coherenceScore", Map.of("type", "number", "description", "Coherence score 0-100"),
                            "vocabularyScore", Map.of("type", "number", "description", "Vocabulary score 0-100"),
                            "overallScore", Map.of("type", "number", "description", "Overall score 0-100"),
                            "levelAssessment", Map.of("type", "string", "description", "CEFR level: a1, a2, b1, b2, c1, or c2"),
                            "generalFeedback", Map.of("type", "string", "description", "Brief constructive feedback"),
                            "corrections", Map.of("type", "array", "items", Map.of("type", "string"), "description", "List of specific corrections")
                    ),
                    "required", List.of("grammarScore", "coherenceScore", "vocabularyScore", "overallScore",
                            "levelAssessment", "generalFeedback", "corrections")
            )
    );

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
        String rubric = WritingRubricProvider.getRubric(level);
        String systemPrompt = "You are an English writing evaluator.\n\n" + rubric +
                "\n\nUse the evaluate_writing tool to return your evaluation.";

        String userMessage = "Prompt: " + exercisePrompt + "\n\nStudent's text:\n" + text;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemPrompt,
                "tools", List.of(EVALUATE_TOOL),
                "tool_choice", Map.of("type", "tool", "name", "evaluate_writing"),
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

        return extractFeedbackFromToolUse(response);
    }

    @SuppressWarnings("unchecked")
    private WritingFeedback extractFeedbackFromToolUse(Map<String, Object> response) throws Exception {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (content == null) throw new Exception("No content in Claude API response");

        for (Map<String, Object> block : content) {
            if ("tool_use".equals(block.get("type"))) {
                Object input = block.get("input");
                String json = MAPPER.writeValueAsString(input);
                return MAPPER.readValue(json, WritingFeedback.class);
            }
        }

        log.warn("No tool_use block found in response: {}", response);
        return WritingFeedback.empty();
    }
}
