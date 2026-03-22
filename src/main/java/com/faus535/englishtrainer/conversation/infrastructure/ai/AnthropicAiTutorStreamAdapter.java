package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.faus535.englishtrainer.conversation.domain.AiTutorStreamPort;
import com.faus535.englishtrainer.conversation.domain.ConversationLevel;
import com.faus535.englishtrainer.conversation.domain.ConversationTurn;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class AnthropicAiTutorStreamAdapter implements AiTutorStreamPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicAiTutorStreamAdapter.class);

    private final WebClient webClient;
    private final String model;
    private final int maxTokens;

    AnthropicAiTutorStreamAdapter(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model:claude-sonnet-4-20250514}") String model,
            @Value("${anthropic.max-tokens:500}") int maxTokens) {
        this.model = model;
        this.maxTokens = maxTokens;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public Flux<String> chatStream(ConversationLevel level, String topic,
                                    List<ConversationTurn> turns, Float confidence) throws AiTutorException {
        String systemPrompt = SystemPromptBuilder.build(level, topic, confidence);
        List<Map<String, String>> messages = buildMessages(turns);

        if (messages.isEmpty()) {
            messages.add(Map.of("role", "user",
                    "content", "Start the conversation. Greet the student and introduce the topic."));
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "stream", true,
                "system", systemPrompt,
                "messages", messages
        );

        return webClient.post()
                .uri("/messages")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(chunk -> chunk.contains("\"type\":\"content_block_delta\""))
                .map(this::extractText)
                .filter(text -> !text.isEmpty())
                .doOnError(e -> log.error("Streaming error: {}", e.getMessage()));
    }

    private List<Map<String, String>> buildMessages(List<ConversationTurn> turns) {
        List<Map<String, String>> messages = new ArrayList<>();
        for (ConversationTurn turn : turns) {
            messages.add(Map.of("role", turn.role(), "content", turn.content()));
        }
        return messages;
    }

    private String extractText(String chunk) {
        int textStart = chunk.indexOf("\"text\":\"");
        if (textStart < 0) return "";
        textStart += 8;
        int textEnd = chunk.indexOf("\"", textStart);
        if (textEnd < 0) return "";
        return chunk.substring(textStart, textEnd)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");
    }
}
