package com.faus535.englishtrainer.vocabularycontext.infrastructure.ai;

import com.faus535.englishtrainer.vocabularycontext.domain.ContextGeneratorPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
class AnthropicContextGeneratorAdapter implements ContextGeneratorPort {

    private final WebClient webClient;

    AnthropicContextGeneratorAdapter(@Value("${anthropic.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }

    @Override
    public String generateSentences(String word, String level) throws Exception {
        String levelConstraints = getLevelConstraints(level);
        String prompt = String.format(
                "Generate 2-3 example sentences for the English word \"%s\" at CEFR level %s.\n\n" +
                "LEVEL CONSTRAINTS:\n%s\n\n" +
                "Return ONLY a JSON array with no additional text. Each element must have: " +
                "\"text\" (the full sentence) and \"highlight\" (an array of two integers [start, end] " +
                "representing the character positions where the word appears in the sentence). " +
                "Example format: [{\"text\": \"sentence here\", \"highlight\": [0, 5]}]",
                word, level.toUpperCase(), levelConstraints
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

    private String getLevelConstraints(String level) {
        return switch (level.toLowerCase()) {
            case "a1" -> "- Use only present simple tense.\n- Max 5 words per sentence.\n- Use only top-300 frequency words (except the target word).";
            case "a2" -> "- Use present simple or past simple only.\n- Max 8 words per sentence.\n- Use only top-800 frequency words (except the target word).";
            case "b1" -> "- Any basic tense allowed.\n- Max 12 words per sentence.\n- Can include one phrasal verb.";
            case "b2" -> "- Any tense including passive allowed.\n- Max 16 words per sentence.\n- Can include idioms and collocations.";
            case "c1" -> "- Any grammatical structure allowed.\n- Natural sentence length.\n- Use academic register.";
            case "c2" -> "- Any structure allowed.\n- Showcase the word in a nuanced, context-rich sentence.\n- Use sophisticated, varied vocabulary.";
            default -> "- Appropriate for intermediate level.";
        };
    }
}
