package com.faus535.englishtrainer.tutorerror.infrastructure.ai;

import com.faus535.englishtrainer.tutorerror.domain.ExerciseGeneratorPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
class AnthropicExerciseGeneratorAdapter implements ExerciseGeneratorPort {

    private final WebClient webClient;

    AnthropicExerciseGeneratorAdapter(@Value("${anthropic.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }

    @Override
    public String generateExercise(String originalText, String correctedText, String errorType) throws Exception {
        String prompt = String.format(
                "A student made the following %s error in English:\n" +
                "- Original (incorrect): \"%s\"\n" +
                "- Corrected: \"%s\"\n\n" +
                "Generate a reinforcement exercise to help the student practice this correction. " +
                "Choose either a fill-the-gap or multiple-choice exercise. " +
                "Return ONLY a JSON object with no additional text, using this format:\n" +
                "{\n" +
                "  \"type\": \"fill-the-gap\" or \"multiple-choice\",\n" +
                "  \"question\": \"the exercise question with ___ for blanks if fill-the-gap\",\n" +
                "  \"options\": [\"option1\", \"option2\", \"option3\", \"option4\"],\n" +
                "  \"correctAnswer\": \"the correct option\"\n" +
                "}",
                errorType, originalText, correctedText
        );

        Map<String, Object> requestBody = Map.of(
                "model", "claude-haiku-4-5-20251001",
                "max_tokens", 500,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        Map<String, Object> response = webClient.post()
                .uri("/messages")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("content")) {
            throw new Exception("Empty response from Anthropic API");
        }

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (content.isEmpty()) {
            throw new Exception("No content in Anthropic API response");
        }

        return (String) content.get(0).get("text");
    }
}
