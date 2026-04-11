package com.faus535.englishtrainer.talk.domain;

import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.domain.event.TalkConversationCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public final class TalkConversation extends AggregateRoot<TalkConversationId> {

    private static final int MAX_TURNS = 30;
    private static final int QUICK_USER_MESSAGE_LIMIT = 3;

    private final TalkConversationId id;
    private final UUID userId;
    private final UUID scenarioId;
    private final TalkLevel level;
    private final ConversationMode mode;
    private final TalkStatus status;
    private final String summary;
    private final TalkEvaluation evaluation;
    private final List<GrammarNote> grammarNotes;
    private final List<VocabItem> newVocabulary;
    private final Instant startedAt;
    private final Instant endedAt;
    private final List<TalkMessage> messages;

    private TalkConversation(TalkConversationId id, UUID userId, UUID scenarioId, TalkLevel level,
                             ConversationMode mode, TalkStatus status, String summary, TalkEvaluation evaluation,
                             List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary,
                             Instant startedAt, Instant endedAt, List<TalkMessage> messages) {
        this.id = id;
        this.userId = userId;
        this.scenarioId = scenarioId;
        this.level = level;
        this.mode = mode;
        this.status = status;
        this.summary = summary;
        this.evaluation = evaluation;
        this.grammarNotes = grammarNotes == null ? null : Collections.unmodifiableList(new ArrayList<>(grammarNotes));
        this.newVocabulary = newVocabulary == null ? null : Collections.unmodifiableList(new ArrayList<>(newVocabulary));
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.messages = Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public static TalkConversation start(UUID userId, UUID scenarioId, TalkLevel level, ConversationMode mode) {
        return new TalkConversation(
                TalkConversationId.generate(), userId, scenarioId, level, mode,
                TalkStatus.ACTIVE, null, null, null, null, Instant.now(), null, List.of());
    }

    public static TalkConversation reconstitute(TalkConversationId id, UUID userId, UUID scenarioId,
                                                 TalkLevel level, ConversationMode mode, TalkStatus status,
                                                 String summary, TalkEvaluation evaluation,
                                                 List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary,
                                                 Instant startedAt, Instant endedAt, List<TalkMessage> messages) {
        return new TalkConversation(id, userId, scenarioId, level, mode, status, summary, evaluation,
                grammarNotes, newVocabulary, startedAt, endedAt, messages);
    }

    public TalkConversation addMessage(TalkMessage message) throws TalkConversationAlreadyEndedException {
        if (status == TalkStatus.COMPLETED) {
            throw new TalkConversationAlreadyEndedException(id);
        }
        List<TalkMessage> newMessages = new ArrayList<>(messages);
        newMessages.add(message);
        return new TalkConversation(id, userId, scenarioId, level, mode, status, summary, evaluation,
                grammarNotes, newVocabulary, startedAt, endedAt, newMessages);
    }

    public TalkConversation end(String summary, TalkEvaluation evaluation)
            throws TalkConversationAlreadyEndedException {
        if (status == TalkStatus.COMPLETED) {
            throw new TalkConversationAlreadyEndedException(id);
        }

        List<TalkCorrection> corrections = IntStream.range(0, messages.size())
                .filter(i -> {
                    TalkCorrection c = messages.get(i).correction();
                    return c != null && c.hasCorrections();
                })
                .mapToObj(i -> {
                    TalkCorrection c = messages.get(i).correction();
                    String originalUserMessage = null;
                    for (int j = i - 1; j >= 0; j--) {
                        if ("user".equals(messages.get(j).role())) {
                            originalUserMessage = messages.get(j).content();
                            break;
                        }
                    }
                    return new TalkCorrection(c.grammarFixes(), c.vocabularySuggestions(),
                            c.pronunciationTips(), c.encouragement(), originalUserMessage);
                })
                .toList();

        TalkConversation ended = new TalkConversation(id, userId, scenarioId, level, mode,
                TalkStatus.COMPLETED, summary, evaluation, grammarNotes, newVocabulary, startedAt, Instant.now(), messages);
        ended.registerEvent(new TalkConversationCompletedEvent(id, userId, corrections, messages.size()));
        return ended;
    }

    public boolean isAtQuickLimit() {
        return mode == ConversationMode.QUICK && userMessages().size() >= QUICK_USER_MESSAGE_LIMIT;
    }

    private List<TalkMessage> userMessages() {
        return messages.stream().filter(m -> "user".equals(m.role())).toList();
    }

    public List<TalkMessage> recentMessages(int maxMessages) {
        if (messages.size() <= maxMessages) {
            return messages;
        }
        return messages.subList(messages.size() - maxMessages, messages.size());
    }

    public boolean isAtMaxTurns() {
        return messages.size() >= MAX_TURNS;
    }

    public int errorCount() {
        return (int) messages.stream()
                .map(TalkMessage::correction)
                .filter(c -> c != null && c.hasCorrections())
                .count();
    }

    public boolean hasGrammarFeedback() { return grammarNotes != null; }

    public TalkConversation withGrammarFeedback(List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary) {
        return new TalkConversation(id, userId, scenarioId, level, mode, status, summary, evaluation,
                grammarNotes, newVocabulary, startedAt, endedAt, messages);
    }

    public TalkConversationId id() { return id; }
    public UUID userId() { return userId; }
    public UUID scenarioId() { return scenarioId; }
    public TalkLevel level() { return level; }
    public ConversationMode mode() { return mode; }
    public TalkStatus status() { return status; }
    public String summary() { return summary; }
    public TalkEvaluation evaluation() { return evaluation; }
    public List<GrammarNote> grammarNotes() { return grammarNotes; }
    public List<VocabItem> newVocabulary() { return newVocabulary; }
    public Instant startedAt() { return startedAt; }
    public Instant endedAt() { return endedAt; }
    public List<TalkMessage> messages() { return messages; }
}
