package com.faus535.englishtrainer.talk.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
class AnthropicTalkAiAdapter implements TalkAiPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicTalkAiAdapter.class);
    private static final Pattern FEEDBACK_PATTERN =
            Pattern.compile("<<F>>(.*?)<<F>>", Pattern.DOTALL);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final int GREETING_MAX_TOKENS = 150;
    private static final int NO_FEEDBACK_MAX_TOKENS = 200;
    private static final int SUMMARY_MAX_TOKENS = 200;

    private static final Map<String, Object> TOOL_EVALUATE_TALK = Map.of(
            "name", "evaluate_talk",
            "description", "Evaluate English conversation",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "grammarAccuracy", Map.of("type", "integer", "description", "0-100"),
                            "vocabularyRange", Map.of("type", "integer", "description", "0-100"),
                            "fluency", Map.of("type", "integer", "description", "0-100"),
                            "taskCompletion", Map.of("type", "integer", "description", "0-100"),
                            "overallScore", Map.of("type", "integer", "description", "0-100"),
                            "levelDemonstrated", Map.of("type", "string", "description", "CEFR level"),
                            "strengths", Map.of("type", "array", "items", Map.of("type", "string")),
                            "areasToImprove", Map.of("type", "array", "items", Map.of("type", "string"))
                    ),
                    "required", List.of("grammarAccuracy", "vocabularyRange", "fluency", "taskCompletion",
                            "overallScore", "levelDemonstrated", "strengths", "areasToImprove")
            )
    );

    private final RestClient restClient;
    private final String model;
    private final int maxTokens;

    AnthropicTalkAiAdapter(
            @Qualifier("anthropicRestClient") RestClient restClient,
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model,
            @Value("${anthropic.max-tokens:300}") int maxTokens) {
        this.restClient = restClient;
        this.model = model;
        this.maxTokens = maxTokens;
    }

    @Override
    public TalkAiResponse chat(TalkLevel level, TalkScenario scenario, List<TalkMessage> messages,
                                Float confidence) throws TalkAiException {
        List<Map<String, String>> apiMessages = buildMessages(messages);

        if (apiMessages.isEmpty()) {
            apiMessages.add(Map.of("role", "user",
                    "content", "Start. Greet the student and introduce the scenario."));
            String systemPrompt = TalkSystemPromptBuilder.build(level, scenario, confidence, false);
            String rawResponse = callClaude(systemPrompt, apiMessages, GREETING_MAX_TOKENS);
            return parseResponse(rawResponse);
        }

        long userTurnCount = messages.stream().filter(m -> "user".equals(m.role())).count();
        boolean includeFeedback = shouldIncludeFeedback(level, userTurnCount);
        String systemPrompt = TalkSystemPromptBuilder.build(level, scenario, confidence, includeFeedback);
        int tokens = includeFeedback ? maxTokens : NO_FEEDBACK_MAX_TOKENS;
        String rawResponse = callClaude(systemPrompt, apiMessages, tokens);
        return parseResponse(rawResponse);
    }

    @Override
    public String summarize(TalkLevel level, List<TalkMessage> messages) throws TalkAiException {
        String systemPrompt = TalkSystemPromptBuilder.buildSummaryPrompt();
        StringBuilder conversationText = new StringBuilder("Conversation:\n");
        for (TalkMessage msg : messages) {
            conversationText.append(msg.role().charAt(0)).append(":").append(msg.content()).append("\n");
        }

        List<Map<String, String>> apiMessages = List.of(
                Map.of("role", "user", "content", conversationText.toString()));

        return callClaude(systemPrompt, apiMessages, SUMMARY_MAX_TOKENS);
    }

    @Override
    public QuickSummary quickSummarize(List<TalkMessage> messages) throws TalkAiException {
        try {
            StringBuilder conversationText = new StringBuilder();
            for (TalkMessage msg : messages) {
                conversationText.append(msg.role()).append(": ").append(msg.content()).append("\n");
            }

            String systemPrompt = """
                    You are an English language coach. Given a short conversation, evaluate it and respond ONLY with valid JSON (no markdown, no explanation):
                    {"taskCompleted": true/false, "top3Corrections": ["original → corrected"], "encouragementNote": "..."}
                    """;

            List<Map<String, String>> apiMessages = List.of(
                    Map.of("role", "user", "content", "Conversation:\n" + conversationText));

            String raw = callClaude(systemPrompt, apiMessages, 200);
            return MAPPER.readValue(raw.trim(), QuickSummary.class);
        } catch (TalkAiException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to parse quick summary, returning default: {}", e.getMessage());
            return new QuickSummary(true, List.of(), "Well done! Keep practicing.");
        }
    }

    private static final Map<String, Object> TOOL_ANALYZE_GRAMMAR = Map.of(
            "name", "analyze_grammar_and_vocabulary",
            "description", "Analyze grammar errors and new vocabulary from user messages",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "grammarNotes", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "properties", Map.of(
                                                    "originalText", Map.of("type", "string"),
                                                    "correction", Map.of("type", "string"),
                                                    "explanation", Map.of("type", "string")
                                            ),
                                            "required", List.of("originalText", "correction", "explanation")
                                    ),
                                    "description", "Up to 5 grammar corrections"
                            ),
                            "newVocabulary", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "properties", Map.of(
                                                    "word", Map.of("type", "string"),
                                                    "definition", Map.of("type", "string"),
                                                    "usedInContext", Map.of("type", "string")
                                            ),
                                            "required", List.of("word", "definition", "usedInContext")
                                    ),
                                    "description", "Up to 5 new vocabulary items"
                            )
                    ),
                    "required", List.of("grammarNotes", "newVocabulary")
            )
    );

    @Override
    public GrammarFeedback analyzeGrammarAndVocabulary(List<TalkMessage> userMessages) throws TalkAiException {
        try {
            if (userMessages.isEmpty()) {
                return new GrammarFeedback(List.of(), List.of());
            }

            StringBuilder conversationText = new StringBuilder();
            for (TalkMessage msg : userMessages) {
                conversationText.append("User: ").append(msg.content()).append("\n");
            }

            Map<String, Object> requestBody = new java.util.HashMap<>(Map.of(
                    "model", model,
                    "max_tokens", 600,
                    "system", List.of(Map.of(
                            "type", "text",
                            "text", "You are an English language coach. Analyze the user's messages for grammar errors and new vocabulary. " +
                                    "Identify up to 5 grammar mistakes with corrections, and up to 5 advanced vocabulary words the user attempted to use.",
                            "cache_control", Map.of("type", "ephemeral")
                    )),
                    "tools", List.of(TOOL_ANALYZE_GRAMMAR),
                    "tool_choice", Map.of("type", "tool", "name", "analyze_grammar_and_vocabulary"),
                    "messages", List.of(Map.of("role", "user", "content", conversationText.toString()))
            ));

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) return new GrammarFeedback(List.of(), List.of());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            for (Map<String, Object> block : content) {
                if ("tool_use".equals(block.get("type"))) {
                    String json = MAPPER.writeValueAsString(block.get("input"));
                    Map<?, ?> result = MAPPER.readValue(json, Map.class);
                    List<GrammarNote> grammarNotes = parseGrammarNotes(result.get("grammarNotes"));
                    List<VocabItem> vocab = parseVocabItems(result.get("newVocabulary"));
                    return new GrammarFeedback(grammarNotes, vocab);
                }
            }
            return new GrammarFeedback(List.of(), List.of());
        } catch (Exception e) {
            log.error("Failed to analyze grammar and vocabulary: {}", e.getMessage(), e);
            throw new TalkAiException("Talk AI service unavailable", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<GrammarNote> parseGrammarNotes(Object raw) {
        if (!(raw instanceof List<?> list)) return List.of();
        return list.stream()
                .filter(item -> item instanceof Map)
                .map(item -> {
                    Map<String, Object> m = (Map<String, Object>) item;
                    return new GrammarNote(
                            String.valueOf(m.getOrDefault("originalText", "")),
                            String.valueOf(m.getOrDefault("correction", "")),
                            String.valueOf(m.getOrDefault("explanation", ""))
                    );
                })
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<VocabItem> parseVocabItems(Object raw) {
        if (!(raw instanceof List<?> list)) return List.of();
        return list.stream()
                .filter(item -> item instanceof Map)
                .map(item -> {
                    Map<String, Object> m = (Map<String, Object>) item;
                    return new VocabItem(
                            String.valueOf(m.getOrDefault("word", "")),
                            String.valueOf(m.getOrDefault("definition", "")),
                            String.valueOf(m.getOrDefault("usedInContext", ""))
                    );
                })
                .toList();
    }

    @Override
    public TalkEvaluation evaluate(TalkLevel level, List<TalkMessage> messages) throws TalkAiException {
        try {
            StringBuilder conversationText = new StringBuilder("Conversation (level: ")
                    .append(level.value().toUpperCase()).append("):\n");
            for (TalkMessage msg : messages) {
                conversationText.append(msg.role().charAt(0)).append(": ").append(msg.content()).append("\n");
            }

            Map<String, Object> requestBody = new java.util.HashMap<>(Map.of(
                    "model", model,
                    "max_tokens", 350,
                    "system", List.of(Map.of(
                            "type", "text",
                            "text", "Evaluate the student's English conversation.",
                            "cache_control", Map.of("type", "ephemeral")
                    )),
                    "tools", List.of(TOOL_EVALUATE_TALK),
                    "tool_choice", Map.of("type", "tool", "name", "evaluate_talk"),
                    "messages", List.of(Map.of("role", "user", "content", conversationText.toString()))
            ));

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) return TalkEvaluation.empty();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            for (Map<String, Object> block : content) {
                if ("tool_use".equals(block.get("type"))) {
                    String json = MAPPER.writeValueAsString(block.get("input"));
                    return MAPPER.readValue(json, TalkEvaluation.class);
                }
            }
            return TalkEvaluation.empty();
        } catch (Exception e) {
            log.error("Failed to evaluate talk conversation: {}", e.getMessage(), e);
            return TalkEvaluation.empty();
        }
    }

    private String callClaude(String systemPrompt, List<Map<String, String>> messages, int tokensLimit)
            throws TalkAiException {
        List<Map<String, Object>> systemBlocks = List.of(
                Map.of("type", "text", "text", systemPrompt,
                        "cache_control", Map.of("type", "ephemeral"))
        );

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", tokensLimit,
                "system", systemBlocks,
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
                throw new TalkAiException("Empty response from Claude API");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            if (content == null || content.isEmpty()) {
                throw new TalkAiException("No content in Claude API response");
            }

            return (String) content.getFirst().get("text");
        } catch (TalkAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Claude API call failed: {}", e.getMessage(), e);
            throw new TalkAiException("Talk AI service unavailable", e);
        }
    }

    private static final int RECENT_MSGS_FULL = 4;
    private static final int SUMMARY_THRESHOLD = 5;
    private static final int OLD_MSG_MAX_CHARS = 60;

    private List<Map<String, String>> buildMessages(List<TalkMessage> messages) {
        List<Map<String, String>> apiMessages = new ArrayList<>();
        int cutoff = Math.max(0, messages.size() - RECENT_MSGS_FULL);

        if (cutoff > 0 && messages.size() >= SUMMARY_THRESHOLD) {
            StringBuilder summary = new StringBuilder("[Prior context] ");
            for (int i = 0; i < cutoff; i++) {
                TalkMessage msg = messages.get(i);
                String content = msg.content();
                if (content.length() > OLD_MSG_MAX_CHARS) {
                    content = content.substring(0, OLD_MSG_MAX_CHARS) + "...";
                }
                summary.append(msg.role().charAt(0)).append(":").append(content).append(" ");
            }
            apiMessages.add(Map.of("role", "user", "content", summary.toString().trim()));
            apiMessages.add(Map.of("role", "assistant", "content", "OK, I have the context. Let's continue."));
        } else {
            for (int i = 0; i < cutoff; i++) {
                TalkMessage msg = messages.get(i);
                apiMessages.add(Map.of("role", msg.role(), "content", msg.content()));
            }
        }

        for (int i = cutoff; i < messages.size(); i++) {
            TalkMessage msg = messages.get(i);
            apiMessages.add(Map.of("role", msg.role(), "content", msg.content()));
        }

        return apiMessages;
    }

    @SuppressWarnings("unchecked")
    private TalkAiResponse parseResponse(String rawResponse) {
        Matcher matcher = FEEDBACK_PATTERN.matcher(rawResponse);
        if (matcher.find()) {
            String content = rawResponse.substring(0, matcher.start()).trim();
            String feedbackJson = matcher.group(1).trim();
            try {
                Map<String, Object> map = MAPPER.readValue(feedbackJson, Map.class);
                List<String> grammar = toStringList(map.getOrDefault("g", map.get("grammarCorrections")));
                List<String> vocab = toStringList(map.getOrDefault("v", map.get("vocabularySuggestions")));
                List<String> pronunciation = toStringList(map.getOrDefault("p", map.get("pronunciationTips")));
                String encouragement = (String) map.getOrDefault("e", map.get("encouragement"));
                TalkCorrection correction = new TalkCorrection(grammar, vocab, pronunciation, encouragement, null);
                return new TalkAiResponse(content, correction);
            } catch (Exception e) {
                log.warn("Failed to parse feedback JSON: {}", feedbackJson, e);
                return new TalkAiResponse(content, TalkCorrection.empty());
            }
        }
        return new TalkAiResponse(rawResponse, TalkCorrection.empty());
    }

    private static boolean shouldIncludeFeedback(TalkLevel level, long userTurnCount) {
        if (userTurnCount == 0) return false;
        int frequency = feedbackFrequency(level);
        if (frequency == 0) return false;
        return userTurnCount % frequency == 0;
    }

    private static int feedbackFrequency(TalkLevel level) {
        return switch (level.value()) {
            case "a1" -> 0;
            case "a2" -> 4;
            case "b1" -> 3;
            case "b2" -> 2;
            case "c1" -> 2;
            case "c2" -> 1;
            default -> 3;
        };
    }

    private List<String> toStringList(Object obj) {
        if (obj instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }
}
