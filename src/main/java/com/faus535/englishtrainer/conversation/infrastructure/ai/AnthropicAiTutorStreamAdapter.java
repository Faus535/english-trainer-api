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
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model,
            @Value("${anthropic.max-tokens:300}") int maxTokens) {
        this.model = model;
        this.maxTokens = maxTokens;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("anthropic-beta", "prompt-caching-2024-07-31")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public Flux<String> chatStream(ConversationLevel level, String topic,
                                    List<ConversationTurn> turns, Float confidence) throws AiTutorException {
        String systemPrompt = SystemPromptBuilder.build(level, topic, confidence, false);
        List<Map<String, String>> messages = buildMessages(turns);

        if (messages.isEmpty()) {
            messages.add(Map.of("role", "user",
                    "content", "Start. Greet student, introduce topic."));
        }

        List<Map<String, Object>> systemBlocks = List.of(
                Map.of("type", "text", "text", systemPrompt,
                        "cache_control", Map.of("type", "ephemeral"))
        );

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "stream", true,
                "system", systemBlocks,
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

    private static final int RECENT_TURNS_FULL = 4;
    private static final int SUMMARY_THRESHOLD = 5;
    private static final int OLD_TURN_MAX_CHARS = 60;

    private List<Map<String, String>> buildMessages(List<ConversationTurn> turns) {
        List<Map<String, String>> messages = new ArrayList<>();
        int cutoff = Math.max(0, turns.size() - RECENT_TURNS_FULL);

        if (cutoff > 0 && turns.size() >= SUMMARY_THRESHOLD) {
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
            for (int i = 0; i < cutoff; i++) {
                ConversationTurn turn = turns.get(i);
                messages.add(Map.of("role", turn.role(), "content", turn.content()));
            }
        }

        for (int i = cutoff; i < turns.size(); i++) {
            ConversationTurn turn = turns.get(i);
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
