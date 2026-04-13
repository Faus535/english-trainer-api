package com.faus535.englishtrainer.pronunciation.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationAiPort;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
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

@Component
class AnthropicPronunciationAiAdapter implements PronunciationAiPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicPronunciationAiAdapter.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Map<String, Object> TOOL_ANALYZE_PRONUNCIATION = Map.of(
            "name", "analyze_pronunciation",
            "description", "Analyze English pronunciation for a Spanish-speaking student",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "ipa", Map.of("type", "string", "description", "IPA transcription"),
                            "syllables", Map.of("type", "string", "description", "Syllable breakdown (e.g. 'thought')"),
                            "stressPattern", Map.of("type", "string", "description", "Stress pattern description"),
                            "tips", Map.of("type", "array", "items", Map.of("type", "string"),
                                    "description", "Pronunciation tips for Spanish speakers"),
                            "commonMistakes", Map.of("type", "array", "items", Map.of("type", "string"),
                                    "description", "Common mistakes Spanish speakers make"),
                            "minimalPairs", Map.of("type", "array", "items", Map.of("type", "string"),
                                    "description", "Minimal pairs to practice (e.g. 'thought/taught')"),
                            "exampleSentences", Map.of("type", "array", "items", Map.of("type", "string"),
                                    "description", "Example sentences using the word")
                    ),
                    "required", List.of("ipa", "syllables", "stressPattern", "tips",
                            "commonMistakes", "minimalPairs", "exampleSentences")
            )
    );

    private static final Map<String, Object> TOOL_EVALUATE_FEEDBACK = Map.of(
            "name", "evaluate_pronunciation_feedback",
            "description", "Evaluate pronunciation feedback comparing target vs recognized text",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "overallScore", Map.of("type", "integer", "description", "Overall score 0-100"),
                            "wordFeedback", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "properties", Map.of(
                                                    "word", Map.of("type", "string"),
                                                    "recognized", Map.of("type", "string"),
                                                    "tip", Map.of("type", "string"),
                                                    "score", Map.of("type", "integer")
                                            ),
                                            "required", List.of("word", "recognized", "tip", "score")
                                    )
                            ),
                            "overallTip", Map.of("type", "string", "description", "Overall pronunciation tip")
                    ),
                    "required", List.of("overallScore", "wordFeedback", "overallTip")
            )
    );

    private static final Map<String, Object> TOOL_DRILL_FEEDBACK = Map.of(
            "name", "evaluate_drill_feedback",
            "description", "Score a pronunciation drill attempt",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "score", Map.of("type", "integer", "description", "Score 0-100"),
                            "feedback", Map.of("type", "string", "description", "Specific feedback")
                    ),
                    "required", List.of("score", "feedback")
            )
    );

    private static final Map<String, Object> TOOL_START_MINI_CONVERSATION = Map.of(
            "name", "start_mini_conversation",
            "description", "Start a pronunciation mini-conversation",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "prompt", Map.of("type", "string", "description", "Conversational prompt for the student"),
                            "targetPhrase", Map.of("type", "string", "description", "Target phrase the student should say")
                    ),
                    "required", List.of("prompt", "targetPhrase")
            )
    );

    private static final Map<String, Object> TOOL_EVALUATE_TURN = Map.of(
            "name", "evaluate_mini_conversation_turn",
            "description", "Evaluate a mini-conversation turn and continue the conversation",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "score", Map.of("type", "integer", "description", "Score 0-100"),
                            "wordFeedback", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "object",
                                            "properties", Map.of(
                                                    "word", Map.of("type", "string"),
                                                    "recognized", Map.of("type", "string"),
                                                    "tip", Map.of("type", "string"),
                                                    "score", Map.of("type", "integer")
                                            ),
                                            "required", List.of("word", "recognized", "tip", "score")
                                    )
                            ),
                            "nextPrompt", Map.of("type", "string", "description", "Next conversational prompt"),
                            "nextTargetPhrase", Map.of("type", "string", "description", "Next target phrase"),
                            "isComplete", Map.of("type", "boolean", "description", "Whether the conversation is done")
                    ),
                    "required", List.of("score", "wordFeedback", "nextPrompt", "nextTargetPhrase", "isComplete")
            )
    );

    private final RestClient restClient;
    private final String model;

    AnthropicPronunciationAiAdapter(
            @Qualifier("anthropicRestClient") RestClient restClient,
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model) {
        this.restClient = restClient;
        this.model = model;
    }

    @Override
    public PronunciationAnalysisResult analyze(String text, String level) throws PronunciationAiException {
        try {
            String systemPrompt = PronunciationPromptBuilder.buildAnalyzeSystemPrompt(level);
            Map<String, Object> requestBody = buildToolUseRequest(systemPrompt,
                    "Analyze the pronunciation of: " + text,
                    TOOL_ANALYZE_PRONUNCIATION, "analyze_pronunciation", 500);

            Map<String, Object> toolInput = callClaudeToolUse(requestBody);

            return new PronunciationAnalysisResult(
                    getString(toolInput, "ipa"),
                    getString(toolInput, "syllables"),
                    getString(toolInput, "stressPattern"),
                    getStringList(toolInput, "tips"),
                    getStringList(toolInput, "commonMistakes"),
                    getStringList(toolInput, "minimalPairs"),
                    getStringList(toolInput, "exampleSentences")
            );
        } catch (PronunciationAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to analyze pronunciation: {}", e.getMessage(), e);
            throw new PronunciationAiException("Pronunciation AI service unavailable", e);
        }
    }

    @Override
    public PronunciationFeedbackResult feedback(String targetText, String recognizedText,
            List<WordConfidence> wordConfidences) throws PronunciationAiException {
        try {
            String systemPrompt = PronunciationPromptBuilder.buildFeedbackSystemPrompt();
            StringBuilder userMessage = new StringBuilder()
                    .append("Target: ").append(targetText).append("\n")
                    .append("Recognized: ").append(recognizedText).append("\n")
                    .append("Word confidences: ");
            for (WordConfidence wc : wordConfidences) {
                userMessage.append(wc.word()).append("(").append(String.format("%.2f", wc.confidence())).append(") ");
            }

            Map<String, Object> requestBody = buildToolUseRequest(systemPrompt,
                    userMessage.toString(),
                    TOOL_EVALUATE_FEEDBACK, "evaluate_pronunciation_feedback", 600);

            Map<String, Object> toolInput = callClaudeToolUse(requestBody);

            int score = ((Number) toolInput.getOrDefault("overallScore", 0)).intValue();
            String overallTip = getString(toolInput, "overallTip");
            List<WordFeedback> wordFeedback = parseWordFeedback(toolInput.get("wordFeedback"));

            return new PronunciationFeedbackResult(score, wordFeedback, overallTip);
        } catch (PronunciationAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to evaluate pronunciation feedback: {}", e.getMessage(), e);
            throw new PronunciationAiException("Pronunciation AI service unavailable", e);
        }
    }

    @Override
    public DrillFeedbackResult drillFeedback(String drillPhrase, String recognizedText,
            double confidence) throws PronunciationAiException {
        try {
            String systemPrompt = PronunciationPromptBuilder.buildDrillFeedbackSystemPrompt();
            String userMessage = "Drill phrase: " + drillPhrase + "\nRecognized: " + recognizedText +
                    "\nConfidence: " + String.format("%.2f", confidence);

            Map<String, Object> requestBody = buildToolUseRequest(systemPrompt,
                    userMessage, TOOL_DRILL_FEEDBACK, "evaluate_drill_feedback", 200);

            Map<String, Object> toolInput = callClaudeToolUse(requestBody);

            int score = ((Number) toolInput.getOrDefault("score", 0)).intValue();
            String feedback = getString(toolInput, "feedback");

            return new DrillFeedbackResult(score, feedback);
        } catch (PronunciationAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to evaluate drill feedback: {}", e.getMessage(), e);
            throw new PronunciationAiException("Pronunciation AI service unavailable", e);
        }
    }

    @Override
    public MiniConversationResult startMiniConversation(String focus, String level) throws PronunciationAiException {
        try {
            String systemPrompt = PronunciationPromptBuilder.buildMiniConversationSystemPrompt(focus, level);
            String userMessage = "Start a pronunciation mini-conversation focused on: " + focus;

            Map<String, Object> requestBody = buildToolUseRequest(systemPrompt,
                    userMessage, TOOL_START_MINI_CONVERSATION, "start_mini_conversation", 300);

            Map<String, Object> toolInput = callClaudeToolUse(requestBody);

            return new MiniConversationResult(
                    getString(toolInput, "prompt"),
                    getString(toolInput, "targetPhrase")
            );
        } catch (PronunciationAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to start mini conversation: {}", e.getMessage(), e);
            throw new PronunciationAiException("Pronunciation AI service unavailable", e);
        }
    }

    @Override
    public MiniConversationTurnResult evaluateMiniConversationTurn(String targetPhrase, String recognizedText,
            List<WordConfidence> wordConfidences, String focus, String level) throws PronunciationAiException {
        try {
            String systemPrompt = PronunciationPromptBuilder.buildMiniConversationTurnSystemPrompt(focus, level);
            StringBuilder userMessage = new StringBuilder()
                    .append("Target phrase: ").append(targetPhrase).append("\n")
                    .append("Recognized: ").append(recognizedText).append("\n")
                    .append("Word confidences: ");
            for (WordConfidence wc : wordConfidences) {
                userMessage.append(wc.word()).append("(").append(String.format("%.2f", wc.confidence())).append(") ");
            }

            Map<String, Object> requestBody = buildToolUseRequest(systemPrompt,
                    userMessage.toString(), TOOL_EVALUATE_TURN, "evaluate_mini_conversation_turn", 500);

            Map<String, Object> toolInput = callClaudeToolUse(requestBody);

            int score = ((Number) toolInput.getOrDefault("score", 0)).intValue();
            List<WordFeedback> wordFeedback = parseWordFeedback(toolInput.get("wordFeedback"));
            String nextPrompt = getString(toolInput, "nextPrompt");
            String nextTargetPhrase = getString(toolInput, "nextTargetPhrase");
            boolean isComplete = Boolean.TRUE.equals(toolInput.get("isComplete"));

            return new MiniConversationTurnResult(score, wordFeedback, nextPrompt, nextTargetPhrase, isComplete);
        } catch (PronunciationAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to evaluate mini conversation turn: {}", e.getMessage(), e);
            throw new PronunciationAiException("Pronunciation AI service unavailable", e);
        }
    }

    private Map<String, Object> buildToolUseRequest(String systemPrompt, String userMessage,
            Map<String, Object> tool, String toolName, int maxTokens) {
        return new java.util.HashMap<>(Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", List.of(Map.of(
                        "type", "text",
                        "text", systemPrompt,
                        "cache_control", Map.of("type", "ephemeral")
                )),
                "tools", List.of(tool),
                "tool_choice", Map.of("type", "tool", "name", toolName),
                "messages", List.of(Map.of("role", "user", "content", userMessage))
        ));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callClaudeToolUse(Map<String, Object> requestBody) throws PronunciationAiException {
        try {
            Map<String, Object> response = restClient.post()
                    .uri("/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new PronunciationAiException("Empty response from Claude API");
            }

            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            if (content == null || content.isEmpty()) {
                throw new PronunciationAiException("No content in Claude API response");
            }

            for (Map<String, Object> block : content) {
                if ("tool_use".equals(block.get("type"))) {
                    String json = MAPPER.writeValueAsString(block.get("input"));
                    return MAPPER.readValue(json, Map.class);
                }
            }
            throw new PronunciationAiException("No tool_use block in Claude API response");
        } catch (PronunciationAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Claude API call failed: {}", e.getMessage(), e);
            throw new PronunciationAiException("Pronunciation AI service unavailable", e);
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private List<WordFeedback> parseWordFeedback(Object raw) {
        if (!(raw instanceof List<?> list)) return List.of();
        List<WordFeedback> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> m) {
                Map<String, Object> map = (Map<String, Object>) m;
                result.add(new WordFeedback(
                        getString(map, "word"),
                        getString(map, "recognized"),
                        getString(map, "tip"),
                        ((Number) map.getOrDefault("score", 0)).intValue()
                ));
            }
        }
        return result;
    }
}
