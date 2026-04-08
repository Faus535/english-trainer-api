# Backend Plan: Reduce AI Token Consumption Across All Modules

> Generated: 2026-04-08
> Request: Reduce AI token consumption across all modules (immerse, talk, article)

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Article system prompt caching | Add `cache_control: ephemeral` to system blocks in `generateArticle()` and `generateQuestions()` only | Cache all 4 methods | `translateWord` has no system prompt; `correctAnswer` system is too short (~10 words). Only these two meet the 1024-token minimum when combined with a long-enough user message |
| 2 | correctAnswer context reduction | Truncate `articleText` to first 400 chars inside the adapter | Pass only the relevant paragraph via `order_index` | The adapter does not receive `order_index` in its port signature; truncating to leading text is safe and avoids port-breaking changes |
| 3 | ArticleContentSizing levels | Support only b1/b2/c1 (matching article generation scope) with b1 as default fallback | Mirror all 6 CEFR levels like ImmerseContentSizing | Article only generates content at B1–C1; adding a1/a2/c2 would be misleading. Normalise to lowercase, strip whitespace |
| 4 | Shared AnthropicRestClientFactory | Defer to Phase 3; phases 1–2 modify adapters independently | Create factory in Phase 1 | Talk adapter already has `anthropic-beta` header and caching on `callClaude()`. Shared factory is a refactor, not a correctness fix. Phases 1–2 are independent |
| 5 | Talk evaluate() system prompt caching | Convert plain string to cacheable block (same pattern as `callClaude()`) | Leave as-is | evaluate() currently sends `"system": "Evaluate the student's English conversation."` as a plain string — no caching. Converting to a block enables the beta header already present on the RestClient |
| 6 | Static tool constants | Extract all tool `Map.of(...)` definitions as `private static final` fields | Keep inline | Tools are rebuilt on every call. Static constants eliminate repeated allocation of nested maps (~10 KB/request) |
| 7 | Immerse system strings | Convert `processContent()` and `generateContent()` system strings to cacheable blocks | Leave as-is | The beta header is already present on the RestClient but the `system` field is sent as a plain string — caching is not active |
| 8 | translateWord max_tokens | Keep hardcoded 200; use B1 fallback if level is null | Add level param to port | Port signature change is out of scope. 200 tokens is already appropriate for a single translation |

## Analysis

### What already exists

**`AnthropicArticleAiAdapter`** — 4 methods (`generateArticle`, `translateWord`, `generateQuestions`, `correctAnswer`). Constructs its own `RestClient` with `JdkClientHttpRequestFactory` (90 s read timeout). No `anthropic-beta` header. Tool definitions are rebuilt inline per call. System prompts are plain strings. `max_tokens` are hardcoded: 2000 / 200 / 1500 / 600. `correctAnswer` sends the entire article text as context.

**`AnthropicImmerseAiAdapter`** — Has `anthropic-beta` header and uses `ImmerseContentSizing` for token sizing. `buildProcessContentTool()` and `buildGenerateContentTool()` are called per request (not static). `system` field in both calls is sent as a plain string — prompt caching is not active despite the beta header being present.

**`AnthropicTalkAiAdapter`** — Has `anthropic-beta` header. `callClaude()` sends system as a cacheable block array (caching active for `chat` and `summarize`). `evaluate()` sends `system` as a plain string — caching not active. `buildEvaluationTool()` is called per request (not static). RestClient built without `JdkClientHttpRequestFactory` — no explicit read timeout.

**`ImmerseContentSizing`** — Record with `private static final Map` and `forLevel(String)` with null/unknown fallback. Template for `ArticleContentSizing`.

**`TalkSystemPromptBuilder`** — In-memory `ConcurrentHashMap` cache for built prompt strings.

---

## Phases

### Phase 1: Article — Prompt Caching + Level-Based Sizing

**Goal**: Enable Anthropic prompt caching on the article adapter (beta header + cacheable system blocks), extract static tool constants, replace hardcoded `max_tokens` with level-aware sizing, and reduce `correctAnswer` context.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleContentSizing.java`
- `src/test/java/com/faus535/englishtrainer/article/domain/ArticleContentSizingTest.java`

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/ai/AnthropicArticleAiAdapter.java`

**Details**:

#### 1.1 — Create `ArticleContentSizing`

```java
package com.faus535.englishtrainer.article.domain;

import java.util.Map;

public record ArticleContentSizing(
        int generateMaxTokens,
        int translateMaxTokens,
        int questionsMaxTokens,
        int correctAnswerMaxTokens
) {
    private static final Map<String, ArticleContentSizing> SIZINGS = Map.of(
            "b1", new ArticleContentSizing(1400, 200, 1050, 420),
            "b2", new ArticleContentSizing(1600, 200, 1200, 480),
            "c1", new ArticleContentSizing(1800, 200, 1350, 540)
    );

    public static ArticleContentSizing forLevel(String level) {
        if (level == null) return SIZINGS.get("b1");
        ArticleContentSizing sizing = SIZINGS.get(level.trim().toLowerCase());
        return sizing != null ? sizing : SIZINGS.get("b1");
    }
}
```

Token values are ~30% below current hardcoded values (2000 → 1400/1600/1800; 1500 → 1050/1200/1350; 600 → 420/480/540). `translateMaxTokens` stays at 200 (unchanged, already minimal).

#### 1.2 — Create `ArticleContentSizingTest`

Test cases:
- `forLevel_b1_returnsCorrectSizing()` — asserts `generateMaxTokens == 1400`
- `forLevel_b2_returnsCorrectSizing()` — asserts `generateMaxTokens == 1600`
- `forLevel_c1_returnsCorrectSizing()` — asserts `generateMaxTokens == 1800`
- `forLevel_null_defaultsToB1()` — null → B1 sizing
- `forLevel_unknownLevel_defaultsToB1()` — "a1" and "z9" → B1 fallback
- `forLevel_mixedCaseAndWhitespace_normalizes()` — "B1", " b1 " → same as "b1"

#### 1.3 — Modify `AnthropicArticleAiAdapter`

**Constructor**: Add `"anthropic-beta", "prompt-caching-2024-07-31"` to `defaultHeader`.

**Static constants** — extract all 4 tool definitions as `private static final Map<String, Object>`:
- `TOOL_GENERATE_ARTICLE` — the `generate_article` tool
- `TOOL_TRANSLATE_WORD` — the `translate_word` tool
- `TOOL_GENERATE_QUESTIONS` — the `generate_questions` tool
- `TOOL_CORRECT_ANSWER` — the `correct_answer` tool

**Static system prompts** — extract as `private static final String`:
- `SYSTEM_GENERATE_ARTICLE` — current system string for `generateArticle()`

**`generateArticle()`**:
- Use `ArticleContentSizing.forLevel(level).generateMaxTokens()` instead of `2000`
- Replace `"system", systemPrompt` (plain string) with `"system", List.of(Map.of("type", "text", "text", SYSTEM_GENERATE_ARTICLE, "cache_control", Map.of("type", "ephemeral")))`
- Use `TOOL_GENERATE_ARTICLE` constant

**`translateWord()`**:
- Use `TOOL_TRANSLATE_WORD` constant (max_tokens stays 200, no system prompt)

**`generateQuestions()`**:
- Use `ArticleContentSizing.forLevel(level).questionsMaxTokens()` instead of `1500`
- Add a system prompt as cacheable block: `"You are an English language educator. Generate comprehension questions for English learners."`
- Use `TOOL_GENERATE_QUESTIONS` constant

**`correctAnswer()`**:
- Use `ArticleContentSizing.forLevel(null).correctAnswerMaxTokens()` for now (level not available in port signature — use B1 default)
- Truncate `articleText` to at most 400 characters before embedding in user message: `String context = articleText != null && articleText.length() > 400 ? articleText.substring(0, 400) + "..." : articleText;`
- Use `TOOL_CORRECT_ANSWER` constant

**Acceptance criteria**:
- [x] `ArticleContentSizingTest` — all 6 test cases pass
- [x] `AnthropicArticleAiAdapter` constructor has `anthropic-beta` header
- [x] All 4 tool definitions are `private static final` constants (not rebuilt per call)
- [x] `generateArticle()` and `generateQuestions()` send system as a cacheable block array
- [x] `generateArticle()` uses `ArticleContentSizing.forLevel(level).generateMaxTokens()` (not `2000`)
- [x] `generateQuestions()` uses `ArticleContentSizing.forLevel(level).questionsMaxTokens()` (not `1500`)
- [x] `correctAnswer()` truncates `articleText` to ≤ 400 chars
- [x] All 73 existing tests pass (`./gradlew test`)

---

### Phase 2: Immerse — Activate Prompt Caching + Static Tool Constants

**Goal**: Activate Anthropic prompt caching on the immerse adapter (beta header is already present but system is sent as a plain string). Extract tool builders as static constants.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ai/AnthropicImmerseAiAdapter.java`

**Details**:

#### 2.1 — Extract static tool constants

Replace `buildProcessContentTool()` and `buildGenerateContentTool()` instance methods with `private static final Map<String, Object>` constants:
- `TOOL_PROCESS_CONTENT` — the `process_content` tool schema
- `TOOL_GENERATE_CONTENT` — the `generate_content` tool schema

Remove the two builder methods and update call sites to reference the constants directly.

#### 2.2 — Convert `processContent()` system to cacheable block

Current:
```java
"system", "Process English text for learners. Extract vocabulary, annotate difficulty, generate exercises."
```

Replace with:
```java
"system", List.of(Map.of(
    "type", "text",
    "text", "Process English text for learners. Extract vocabulary, annotate difficulty, generate exercises.",
    "cache_control", Map.of("type", "ephemeral")
))
```

Note: This system prompt is ~12 tokens — below Anthropic's 1024-token caching minimum. The block is added for correctness and forward-compatibility (Anthropic does not error on short prompts with `cache_control`; it simply does not cache them).

#### 2.3 — Convert `generateContent()` system to cacheable block

Current: `"system", systemPrompt` where `systemPrompt` comes from `buildGenerateSystemPrompt(contentType)` (a plain string).

Replace with:
```java
"system", List.of(Map.of(
    "type", "text",
    "text", buildGenerateSystemPrompt(contentType),
    "cache_control", Map.of("type", "ephemeral")
))
```

The three system strings (TEXT/AUDIO/VIDEO) are ~25 tokens each — below the cache minimum. Same rationale: added for forward-compatibility.

**Acceptance criteria**:
- [ ] `TOOL_PROCESS_CONTENT` and `TOOL_GENERATE_CONTENT` are `private static final` constants
- [ ] `buildProcessContentTool()` and `buildGenerateContentTool()` methods are removed
- [ ] `processContent()` sends `system` as a cacheable block array (not plain string)
- [ ] `generateContent()` sends `system` as a cacheable block array (not plain string)
- [ ] All 73 existing tests pass (`./gradlew test`)

---

### Phase 3: Talk + Shared RestClient — evaluate() Caching + Timeout Fix

**Goal**: Enable prompt caching on `evaluate()` (currently the only talk method without caching). Extract `buildEvaluationTool()` as a static constant. Create a shared `AnthropicRestClientFactory` to centralise RestClient construction and add the missing read timeout to Talk. Wire all three adapters to use the shared bean.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/shared/infrastructure/config/AnthropicRestClientFactory.java`

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/ai/AnthropicTalkAiAdapter.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/ai/AnthropicArticleAiAdapter.java`
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ai/AnthropicImmerseAiAdapter.java`

**Details**:

#### 3.1 — Create `AnthropicRestClientFactory`

```java
package com.faus535.englishtrainer.shared.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
class AnthropicRestClientFactory {

    @Bean
    RestClient anthropicRestClient(
            @Value("${anthropic.api-key}") String apiKey) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(90));
        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("anthropic-beta", "prompt-caching-2024-07-31")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
```

The factory is package-private (class-level). The `@Bean` method is package-private too. The bean name is `"anthropicRestClient"`.

If `anthropic.api-key` is blank at startup, Spring will inject an empty string — the first API call will fail with a 401. This is acceptable (fail-fast on actual usage, not on startup). Optionally add a `@PostConstruct` validation if strict startup validation is desired — out of scope for this plan.

#### 3.2 — Modify `AnthropicTalkAiAdapter`

**Constructor**: Replace manual `RestClient.builder()` block with `@Qualifier("anthropicRestClient") RestClient restClient` injected parameter. Remove manual header setup.

Before (current constructor builds RestClient without JdkClientHttpRequestFactory):
```java
AnthropicTalkAiAdapter(
    @Value("${anthropic.api-key}") String apiKey,
    @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model,
    @Value("${anthropic.max-tokens:300}") int maxTokens) { ... }
```

After:
```java
AnthropicTalkAiAdapter(
    @Qualifier("anthropicRestClient") RestClient restClient,
    @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model,
    @Value("${anthropic.max-tokens:300}") int maxTokens) {
    this.restClient = restClient;
    this.model = model;
    this.maxTokens = maxTokens;
}
```

**`TOOL_EVALUATE_TALK` static constant**: Extract `buildEvaluationTool()` result as `private static final Map<String, Object> TOOL_EVALUATE_TALK`. Remove the builder method.

**`evaluate()` — convert system to cacheable block**:

Current:
```java
"system", "Evaluate the student's English conversation."
```

Replace with:
```java
"system", List.of(Map.of(
    "type", "text",
    "text", "Evaluate the student's English conversation.",
    "cache_control", Map.of("type", "ephemeral")
))
```

Use `TOOL_EVALUATE_TALK` constant in the request body.

#### 3.3 — Modify `AnthropicArticleAiAdapter`

Replace constructor's manual `RestClient` construction with `@Qualifier("anthropicRestClient") RestClient restClient` injection. Remove `@Value("${anthropic.api-key}")`, `HttpClient`, `JdkClientHttpRequestFactory` construction from the constructor.

#### 3.4 — Modify `AnthropicImmerseAiAdapter`

Same as 3.3 — inject shared RestClient, remove manual construction.

**Acceptance criteria**:
- [ ] `AnthropicRestClientFactory` is a `@Configuration` class in `shared.infrastructure.config`
- [ ] Factory bean uses `JdkClientHttpRequestFactory` with 90 s read timeout
- [ ] Factory bean includes `anthropic-beta: prompt-caching-2024-07-31` header
- [ ] `AnthropicTalkAiAdapter` injects `RestClient` via constructor (no manual `RestClient.builder()`)
- [ ] `AnthropicArticleAiAdapter` injects `RestClient` via constructor (no manual `RestClient.builder()`)
- [ ] `AnthropicImmerseAiAdapter` injects `RestClient` via constructor (no manual `RestClient.builder()`)
- [ ] `TOOL_EVALUATE_TALK` is a `private static final` constant
- [ ] `evaluate()` sends `system` as a cacheable block array
- [ ] All 73 existing tests pass (`./gradlew test`)

---

## API Contract

N/A — no endpoint changes. All port interfaces unchanged.

## Database Changes

N/A — no migrations.

## Testing Strategy

### Unit tests (new)

**`ArticleContentSizingTest`** — 6 cases covering: known levels (b1/b2/c1), null fallback, unknown level fallback, uppercase/mixed-case normalisation, whitespace stripping.

### Regression (existing)

All 73 existing tests must pass after each phase before committing. Run `./gradlew test` after each phase.

### Manual smoke test (recommended after Phase 1)

Call `POST /articles` with a b1 topic and verify the response returns normally. Token counts visible in Anthropic dashboard — baseline before/after comparison recommended.

### What is explicitly not tested

- Prompt caching behaviour (cannot be unit-tested; observable only in Anthropic usage dashboard)
- `AnthropicRestClientFactory` wiring (covered implicitly by Spring Boot startup succeeding in integration tests)
- `correctAnswer()` empty-text edge — adapter currently passes `articleText` as-is; truncation to `""` or null produces an empty context string which Claude handles gracefully. No unit test added (no mocking of RestClient in scope).
