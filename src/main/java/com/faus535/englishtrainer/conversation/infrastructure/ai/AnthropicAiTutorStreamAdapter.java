package com.faus535.englishtrainer.conversation.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.conversation.domain.AiTutorStreamPort;
import com.faus535.englishtrainer.conversation.domain.ConversationLevel;
import com.faus535.englishtrainer.conversation.domain.ConversationTurn;
import com.faus535.englishtrainer.conversation.domain.TutorFeedback;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class AnthropicAiTutorStreamAdapter implements AiTutorStreamPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicAiTutorStreamAdapter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern FEEDBACK_PATTERN = Pattern.compile("<<F>>(.*?)<<F>>", Pattern.DOTALL);

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
    public Flux<StreamEvent> chatStream(ConversationLevel level, String topic,
                                         List<ConversationTurn> turns, Float confidence) throws AiTutorException {
        long userTurnCount = turns.stream().filter(t -> "user".equals(t.role())).count();
        boolean includeFeedback = shouldIncludeFeedback(level, userTurnCount);
        int tokens = includeFeedback ? maxTokens : 200;

        String systemPrompt = SystemPromptBuilder.build(level, topic, confidence, includeFeedback);
        List<Map<String, String>> messages = buildMessages(turns);

        if (messages.isEmpty()) {
            messages.add(Map.of("role", "user", "content", "Start. Greet student, introduce topic."));
        }

        List<Map<String, Object>> systemBlocks = List.of(
                Map.of("type", "text", "text", systemPrompt,
                        "cache_control", Map.of("type", "ephemeral"))
        );

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", tokens,
                "stream", true,
                "system", systemBlocks,
                "messages", messages
        );

        Sinks.Many<StreamEvent> sink = Sinks.many().unicast().onBackpressureBuffer();
        StringBuilder fullText = new StringBuilder();

        webClient.post()
                .uri("/messages")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(chunk -> chunk.contains("\"type\":\"content_block_delta\""))
                .map(this::extractText)
                .filter(text -> !text.isEmpty())
                .doOnNext(text -> {
                    fullText.append(text);
                    // Emit chunk only if we haven't started the feedback block
                    if (!fullText.toString().contains("<<F>>")) {
                        sink.tryEmitNext(new StreamEvent.TextChunk(text));
                    }
                })
                .doOnComplete(() -> {
                    String accumulated = fullText.toString();
                    Matcher matcher = FEEDBACK_PATTERN.matcher(accumulated);
                    if (matcher.find()) {
                        // Emit any text before the feedback block that wasn't emitted yet
                        String contentBeforeFeedback = accumulated.substring(0, matcher.start()).trim();
                        String alreadyEmitted = accumulated.substring(0,
                                accumulated.indexOf("<<F>>")).trim();
                        // Content and alreadyEmitted should be the same, but just in case
                        String cleanContent = contentBeforeFeedback;

                        TutorFeedback feedback = parseFeedback(matcher.group(1).trim());
                        sink.tryEmitNext(new StreamEvent.Feedback(feedback, cleanContent));
                    } else {
                        sink.tryEmitNext(new StreamEvent.Feedback(TutorFeedback.empty(), accumulated.trim()));
                    }
                    sink.tryEmitComplete();
                })
                .doOnError(e -> {
                    log.error("Streaming error: {}", e.getMessage());
                    String accumulated = fullText.toString().trim();
                    if (!accumulated.isEmpty()) {
                        sink.tryEmitNext(new StreamEvent.Feedback(TutorFeedback.empty(), accumulated));
                    }
                    sink.tryEmitComplete();
                })
                .subscribe();

        return sink.asFlux();
    }

    static boolean shouldIncludeFeedback(ConversationLevel level, long userTurnCount) {
        if (userTurnCount == 0) return false;
        int frequency = feedbackFrequency(level);
        return userTurnCount % frequency == 0;
    }

    static int feedbackFrequency(ConversationLevel level) {
        return switch (level.value()) {
            case "a1", "a2" -> 2;
            case "b1", "b2" -> 3;
            default -> 5;
        };
    }

    @SuppressWarnings("unchecked")
    private TutorFeedback parseFeedback(String feedbackJson) {
        try {
            Map<String, Object> map = MAPPER.readValue(feedbackJson, Map.class);
            List<String> grammar = toStringList(map.getOrDefault("g", map.get("grammarCorrections")));
            List<String> vocab = toStringList(map.getOrDefault("v", map.get("vocabularySuggestions")));
            List<String> pronunciation = toStringList(map.getOrDefault("p", map.get("pronunciationTips")));
            String encouragement = (String) map.getOrDefault("e", map.get("encouragement"));
            return new TutorFeedback(grammar, vocab, pronunciation, encouragement);
        } catch (Exception e) {
            log.warn("Failed to parse stream feedback: {}", feedbackJson, e);
            return TutorFeedback.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> toStringList(Object obj) {
        if (obj instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
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
