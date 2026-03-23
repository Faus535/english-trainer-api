package com.faus535.englishtrainer.exercise.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.exercise.domain.Exercise;
import com.faus535.englishtrainer.exercise.domain.ExerciseGeneratorPort;
import com.faus535.englishtrainer.exercise.domain.ExerciseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
class AnthropicExerciseGeneratorAdapter implements ExerciseGeneratorPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicExerciseGeneratorAdapter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RestClient restClient;
    private final String model;

    AnthropicExerciseGeneratorAdapter(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Exercise> generate(String level, List<String> errors) throws Exception {
        String userMsg = "Student level: %s\nErrors from conversation:\n%s\n\nGenerate 3-5 exercises targeting these errors."
                .formatted(level.toUpperCase(), String.join("\n- ", errors));

        Map<String, Object> tool = buildExercisesTool();
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 800,
                "system", "You generate English practice exercises based on student errors. Mix exercise types: FILL_THE_GAP, SENTENCE_CORRECTION, MULTIPLE_CHOICE, REWRITE.",
                "tools", List.of(tool),
                "tool_choice", Map.of("type", "tool", "name", "generate_exercises"),
                "messages", List.of(Map.of("role", "user", "content", userMsg))
        );

        Map<String, Object> response = restClient.post()
                .uri("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) return List.of();

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        for (Map<String, Object> block : content) {
            if ("tool_use".equals(block.get("type"))) {
                Map<String, Object> input = (Map<String, Object>) block.get("input");
                List<Map<String, Object>> exercisesRaw = (List<Map<String, Object>>) input.get("exercises");
                return exercisesRaw.stream().map(this::mapExercise).toList();
            }
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private Exercise mapExercise(Map<String, Object> raw) {
        ExerciseType type;
        try {
            type = ExerciseType.valueOf((String) raw.get("type"));
        } catch (Exception e) {
            type = ExerciseType.MULTIPLE_CHOICE;
        }
        List<String> options = raw.get("options") instanceof List<?> list
                ? list.stream().map(Object::toString).toList()
                : List.of();
        return new Exercise(
                type,
                (String) raw.get("instruction"),
                (String) raw.get("content"),
                options,
                (String) raw.get("correctAnswer"),
                (String) raw.get("explanation"),
                (String) raw.get("relatedError")
        );
    }

    private Map<String, Object> buildExercisesTool() {
        Map<String, Object> exerciseSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "type", Map.of("type", "string", "description", "FILL_THE_GAP, SENTENCE_CORRECTION, MULTIPLE_CHOICE, or REWRITE"),
                        "instruction", Map.of("type", "string", "description", "Exercise instruction"),
                        "content", Map.of("type", "string", "description", "Exercise content (sentence with gap or error)"),
                        "options", Map.of("type", "array", "items", Map.of("type", "string"), "description", "Options for multiple choice"),
                        "correctAnswer", Map.of("type", "string", "description", "The correct answer"),
                        "explanation", Map.of("type", "string", "description", "Brief explanation of the correct answer"),
                        "relatedError", Map.of("type", "string", "description", "The original error this exercise targets")
                ),
                "required", List.of("type", "instruction", "content", "correctAnswer", "explanation", "relatedError")
        );
        return Map.of(
                "name", "generate_exercises",
                "description", "Generate English practice exercises based on student errors",
                "input_schema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "exercises", Map.of("type", "array", "items", exerciseSchema)
                        ),
                        "required", List.of("exercises")
                )
        );
    }
}
