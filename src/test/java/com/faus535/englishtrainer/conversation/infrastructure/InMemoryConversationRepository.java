package com.faus535.englishtrainer.conversation.infrastructure;

import com.faus535.englishtrainer.conversation.domain.*;

import java.util.*;

public final class InMemoryConversationRepository implements ConversationRepository {

    private final Map<ConversationId, Conversation> store = new HashMap<>();

    @Override
    public Conversation save(Conversation conversation) {
        store.put(conversation.id(), conversation);
        return conversation;
    }

    @Override
    public Optional<Conversation> findById(ConversationId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Conversation> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(c -> c.userId().equals(userId))
                .sorted(Comparator.comparing(Conversation::startedAt).reversed())
                .toList();
    }

    @Override
    public int countActiveByUserId(UUID userId) {
        return (int) store.values().stream()
                .filter(c -> c.userId().equals(userId))
                .filter(c -> c.status() == ConversationStatus.ACTIVE)
                .count();
    }

    public void clear() {
        store.clear();
    }

    public int size() {
        return store.size();
    }
}
