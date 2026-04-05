package com.faus535.englishtrainer.immerse.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.immerse.domain.ImmerseAiPort;
import com.faus535.englishtrainer.immerse.domain.VocabularyItem;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
class AnthropicImmerseAiAdapter implements ImmerseAiPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicImmerseAiAdapter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RestClient restClient;
    private final String model;

    AnthropicImmerseAiAdapter(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("anthropic-beta", "prompt-caching-2024-07-31")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ImmerseProcessResult processContent(String rawText, String level) throws ImmerseAiException {
        try {
            Map<String, Object> tool = buildProcessContentTool();
            String userMessage = "Process this text for a %s level English learner:\n\n%s"
                    .formatted(level != null ? level.toUpperCase() : "B1", rawText);

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "max_tokens", 2000,
                    "system", "You process English text for language learners. Extract vocabulary, annotate difficulty, and generate exercises.",
                    "tools", List.of(tool),
                    "tool_choice", Map.of("type", "tool", "name", "process_content"),
                    "messages", List.of(Map.of("role", "user", "content", userMessage))
            );

            Map<String, Object> response = restClient.post()
                    .uri("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) throw new ImmerseAiException("Empty response from Claude API");

            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            for (Map<String, Object> block : content) {
                if ("tool_use".equals(block.get("type"))) {
                    Map<String, Object> input = (Map<String, Object>) block.get("input");
                    return parseProcessResult(input);
                }
            }
            throw new ImmerseAiException("No tool_use block in Claude response");
        } catch (ImmerseAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Immerse AI processing failed: {}", e.getMessage(), e);
            throw new ImmerseAiException("Content processing failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ImmerseProcessResult parseProcessResult(Map<String, Object> input) {
        String processedText = (String) input.getOrDefault("processedText", "");
        String detectedLevel = (String) input.getOrDefault("detectedLevel", "b1");

        List<VocabularyItem> vocabulary = List.of();
        Object vocabRaw = input.get("vocabulary");
        if (vocabRaw instanceof List<?> vocabList) {
            vocabulary = vocabList.stream()
                    .filter(v -> v instanceof Map)
                    .map(v -> {
                        Map<String, Object> vm = (Map<String, Object>) v;
                        return new VocabularyItem(
                                (String) vm.get("word"),
                                (String) vm.get("definition"),
                                (String) vm.get("exampleSentence"),
                                (String) vm.getOrDefault("cefrLevel", detectedLevel));
                    }).toList();
        }

        List<GeneratedExercise> exercises = List.of();
        Object exRaw = input.get("exercises");
        if (exRaw instanceof List<?> exList) {
            exercises = exList.stream()
                    .filter(e -> e instanceof Map)
                    .map(e -> {
                        Map<String, Object> em = (Map<String, Object>) e;
                        List<String> options = em.get("options") instanceof List<?> opts
                                ? opts.stream().map(Object::toString).toList()
                                : List.of();
                        return new GeneratedExercise(
                                (String) em.get("type"),
                                (String) em.get("question"),
                                (String) em.get("correctAnswer"),
                                options);
                    }).toList();
        }

        return new ImmerseProcessResult(processedText, detectedLevel, vocabulary, exercises);
    }

    private Map<String, Object> buildProcessContentTool() {
        return Map.of(
                "name", "process_content",
                "description", "Process text for English language learning",
                "input_schema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "processedText", Map.of("type", "string", "description", "The original text with annotations"),
                                "detectedLevel", Map.of("type", "string", "description", "Detected CEFR level: a1,a2,b1,b2,c1,c2"),
                                "vocabulary", Map.of("type", "array", "items", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "word", Map.of("type", "string"),
                                                "definition", Map.of("type", "string"),
                                                "exampleSentence", Map.of("type", "string"),
                                                "cefrLevel", Map.of("type", "string")
                                        ))),
                                "exercises", Map.of("type", "array", "items", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "type", Map.of("type", "string", "description", "MULTIPLE_CHOICE, FILL_THE_GAP, TRUE_FALSE, WORD_DEFINITION"),
                                                "question", Map.of("type", "string"),
                                                "correctAnswer", Map.of("type", "string"),
                                                "options", Map.of("type", "array", "items", Map.of("type", "string"))
                                        )))
                        ),
                        "required", List.of("processedText", "detectedLevel", "vocabulary", "exercises")
                )
        );
    }
}
