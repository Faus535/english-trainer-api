# AI Tutor - Backend Implementation Plan

## Overview
New `conversation` module to power an AI English tutor via Claude API.
Turn-based REST: receives user transcript, calls Claude, returns response + structured feedback.

## Database Schema

### conversations
`id` UUID PK, `user_id` UUID FK, `level` VARCHAR(2), `topic` VARCHAR(255) nullable,
`status` VARCHAR(20) [active|completed], `summary` TEXT nullable, `started_at` TIMESTAMP, `ended_at` TIMESTAMP nullable.

### conversation_turns
`id` UUID PK, `conversation_id` UUID FK, `role` VARCHAR(10) [user|assistant],
`content` TEXT, `feedback_json` JSONB nullable, `confidence` FLOAT nullable, `created_at` TIMESTAMP.

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/conversations` | Start conversation `{ level, topic? }` |
| POST | `/api/conversations/{id}/messages` | Send message `{ transcript, confidence }` |
| GET | `/api/conversations/{id}` | Get conversation with all turns |
| GET | `/api/conversations` | List user conversations |
| PUT | `/api/conversations/{id}/end` | End conversation, generate summary |

## Phase 1 — Core module + Claude integration

- [x] Flyway migration: `conversations` + `conversation_turns` tables
- [x] Domain: `Conversation` aggregate, `ConversationTurn` value object, `ConversationId` VO
- [x] Domain: `ConversationRepository` interface, `AiTutorPort` interface
- [x] Application: `StartConversationUseCase` — create conversation, call Claude for opening message
- [x] Application: `SendMessageUseCase` — persist user turn, build messages array, call Claude, persist response
- [x] Application: `GetConversationUseCase`, `ListConversationsUseCase`
- [x] Infrastructure: `AnthropicAiTutorAdapter` — calls Claude Messages API via RestClient
- [x] Infrastructure: JPA entities + repository adapter
- [x] Infrastructure: controllers (one per action, package-private, Jakarta validation)
- [x] Config: `anthropic.api-key`, `anthropic.model` (claude-sonnet), `anthropic.max-tokens` (500)

## Phase 2 — Structured feedback parsing

- [x] System prompt returns `|||FEEDBACK|||{json}|||END_FEEDBACK|||` block after natural response
- [x] `AnthropicAiTutorAdapter` parses response into `content` + `TutorFeedback` object
- [x] `TutorFeedback` record: `grammarCorrections`, `vocabularySuggestions`, `pronunciationTips`, `encouragement`
- [x] Store parsed feedback as JSONB in `conversation_turns.feedback_json`
- [x] Return feedback as separate field in message response DTO

## Phase 3 — Dynamic level adaptation

- [x] System prompt builder that adjusts vocabulary, sentence length, tolerance, and topics per CEFR level
- [x] Include STT confidence in prompt: low confidence → ask clarification instead of correcting
- [x] Level rules: A1-A2 basic/tolerant, B1 moderate, B2-C1 strict with nuance corrections
- [x] Conversation context management: summarize old turns when history exceeds ~30 turns

## Phase 4 — Integration + polish

- [x] `EndConversationUseCase` — call Claude to summarize conversation highlights
- [x] Grant XP on conversation completion (integrate with gamification module)
- [x] Add new vocabulary from feedback to spaced repetition queue (requires extending SR model to word-level)
- [x] Rate limiting: max concurrent conversations per user
- [x] Error handling: Claude API timeouts, rate limits, fallback responses

## Phase 5 — Advanced (future)

- [x] SSE streaming for Claude responses (faster perceived latency)
- [x] Role-play scenarios: job interview, restaurant, doctor visit, travel, hotel check-in, shopping
- [x] Conversation analytics: common mistakes over time, vocabulary growth tracking
- [x] Topic suggestions based on level + past conversations
