package com.faus535.englishtrainer.immerse.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseAiPort;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentSizing;
import com.faus535.englishtrainer.immerse.domain.VocabularyItem;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final Map<String, Object> TOOL_PROCESS_CONTENT = Map.of(
            "name", "process_content",
            "description", "Process English text for learners",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "processedText", Map.of("type", "string", "description", "Annotated text"),
                            "detectedLevel", Map.of("type", "string", "description", "CEFR level"),
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
                                    "properties", Map.ofEntries(
                                            Map.entry("type", Map.of("type", "string", "description", "MULTIPLE_CHOICE|FILL_THE_GAP|TRUE_FALSE|WORD_DEFINITION|LISTENING_CLOZE")),
                                            Map.entry("question", Map.of("type", "string")),
                                            Map.entry("correctAnswer", Map.of("type", "string")),
                                            Map.entry("options", Map.of("type", "array", "items", Map.of("type", "string"))),
                                            Map.entry("listen_text", Map.of("type", "string", "description", "Full sentence for TTS. Required for LISTENING_CLOZE type only.")),
                                            Map.entry("blank_position", Map.of("type", "integer", "description", "0-based word index of the target word in listen_text. Required for LISTENING_CLOZE type only."))
                                    )))
                    ),
                    "required", List.of("processedText", "detectedLevel", "vocabulary", "exercises")
            )
    );

    private static final Map<String, Object> TOOL_GENERATE_CONTENT = Map.of(
            "name", "generate_content",
            "description", "Generate English learning content",
            "input_schema", Map.of(
                    "type", "object",
                    "properties", Map.ofEntries(
                            Map.entry("title", Map.of("type", "string", "description", "Content title")),
                            Map.entry("text", Map.of("type", "string", "description", "Content text")),
                            Map.entry("detectedLevel", Map.of("type", "string", "description", "CEFR level")),
                            Map.entry("vocabulary", Map.of("type", "array", "items", Map.of(
                                    "type", "object",
                                    "properties", Map.of(
                                            "word", Map.of("type", "string"),
                                            "definition", Map.of("type", "string"),
                                            "exampleSentence", Map.of("type", "string", "description", "Example"),
                                            "cefrLevel", Map.of("type", "string")
                                    )))),
                            Map.entry("exercises", Map.of("type", "array", "items", Map.of(
                                    "type", "object",
                                    "properties", Map.ofEntries(
                                            Map.entry("type", Map.of("type", "string", "description", "MULTIPLE_CHOICE|FILL_THE_GAP|TRUE_FALSE|WORD_DEFINITION|LISTENING_CLOZE")),
                                            Map.entry("question", Map.of("type", "string")),
                                            Map.entry("correctAnswer", Map.of("type", "string")),
                                            Map.entry("options", Map.of("type", "array", "items", Map.of("type", "string"))),
                                            Map.entry("listen_text", Map.of("type", "string", "description", "Full sentence for TTS. Required for LISTENING_CLOZE type only.")),
                                            Map.entry("blank_position", Map.of("type", "integer", "description", "0-based word index of the target word in listen_text. Required for LISTENING_CLOZE type only."))
                                    ))))
                    ),
                    "required", List.of("title", "text", "detectedLevel", "vocabulary", "exercises")
            )
    );

    private final RestClient restClient;
    private final String model;

    AnthropicImmerseAiAdapter(
            @Qualifier("anthropicRestClient") RestClient restClient,
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model) {
        this.restClient = restClient;
        this.model = model;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ImmerseProcessResult processContent(String rawText, String level) throws ImmerseAiException {
        try {
            ImmerseContentSizing sizing = ImmerseContentSizing.forLevel(level);
            String userMessage = "Process this text for a %s level English learner:\n\n%s"
                    .formatted(level != null ? level.toUpperCase() : "B1", rawText);

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "max_tokens", sizing.processingMaxTokens(),
                    "system", List.of(Map.of(
                            "type", "text",
                            "text", "Process English text for learners. Extract vocabulary, annotate difficulty, generate exercises. For each vocabulary item, also generate one exercise of type LISTENING_CLOZE: provide listen_text as a complete natural sentence containing the word, and blank_position as the 0-based word index of the target word in the sentence.",
                            "cache_control", Map.of("type", "ephemeral")
                    )),
                    "tools", List.of(TOOL_PROCESS_CONTENT),
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
                                options,
                                (String) em.get("listen_text"),
                                em.get("blank_position") instanceof Number n ? n.intValue() : null);
                    }).toList();
        }

        return new ImmerseProcessResult(processedText, detectedLevel, vocabulary, exercises);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ImmerseGenerateResult generateContent(ContentType contentType, String level, String topic)
            throws ImmerseAiException {
        try {
            ImmerseContentSizing sizing = ImmerseContentSizing.forLevel(level);
            String effectiveLevel = level != null ? level.toUpperCase() : "B1";
            String systemPrompt = buildGenerateSystemPrompt(contentType);
            String userMessage = buildGenerateUserMessage(contentType, effectiveLevel, topic, sizing);

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "max_tokens", sizing.generateMaxTokens(),
                    "system", List.of(Map.of(
                            "type", "text",
                            "text", systemPrompt,
                            "cache_control", Map.of("type", "ephemeral")
                    )),
                    "tools", List.of(TOOL_GENERATE_CONTENT),
                    "tool_choice", Map.of("type", "tool", "name", "generate_content"),
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
                    return parseGenerateResult(input);
                }
            }
            throw new ImmerseAiException("No tool_use block in Claude response");
        } catch (ImmerseAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Immerse AI content generation failed: {}", e.getMessage(), e);
            throw new ImmerseAiException("Content generation failed", e);
        }
    }

    private String buildGenerateSystemPrompt(ContentType contentType) {
        String listeningClozeInstruction = " For each vocabulary item, also generate one exercise of type LISTENING_CLOZE: provide listen_text as a complete natural sentence containing the word, and blank_position as the 0-based word index of the target word in the sentence.";
        return switch (contentType) {
            case TEXT -> "You create English learning content. Generate an engaging article/story at the requested CEFR level with varied vocabulary. Extract key vocabulary and generate exercises." + listeningClozeInstruction;
            case AUDIO -> "You create English learning content. Generate a realistic podcast transcript or dialogue with speaker labels (Host:, Guest:) at the requested CEFR level. Extract key vocabulary and generate exercises." + listeningClozeInstruction;
            case VIDEO -> "You create English learning content. Generate a documentary narration or video script with visual context [Scene: ...] at the requested CEFR level. Extract key vocabulary and generate exercises." + listeningClozeInstruction;
        };
    }

    private String buildGenerateUserMessage(ContentType contentType, String level, String topic,
                                               ImmerseContentSizing sizing) {
        String contentDescription = switch (contentType) {
            case TEXT -> "an article or short story";
            case AUDIO -> "a podcast transcript or dialogue";
            case VIDEO -> "a documentary narration or video script";
        };

        StringBuilder message = new StringBuilder();
        message.append("Generate ").append(contentDescription);
        message.append(" for a ").append(level).append(" level English learner.");

        if (topic != null && !topic.isBlank()) {
            message.append(" Topic: ").append(topic).append(".");
        } else {
            message.append(" Choose an interesting and engaging topic.");
        }

        message.append("\n\nThe content should be %d-%d words.".formatted(sizing.minWords(), sizing.maxWords()));
        message.append(" Generate %d varied exercises (MULTIPLE_CHOICE, FILL_THE_GAP, TRUE_FALSE, WORD_DEFINITION).".formatted(sizing.exerciseCount()));
        message.append(" Extract %d key vocabulary items with definitions and example sentences.".formatted(sizing.vocabCount()));

        return message.toString();
    }

    @SuppressWarnings("unchecked")
    private ImmerseGenerateResult parseGenerateResult(Map<String, Object> input) {
        String title = (String) input.getOrDefault("title", "Generated Content");
        String text = (String) input.getOrDefault("text", "");
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
                                options,
                                (String) em.get("listen_text"),
                                em.get("blank_position") instanceof Number n ? n.intValue() : null);
                    }).toList();
        }

        return new ImmerseGenerateResult(title, text, detectedLevel, vocabulary, exercises);
    }
}
