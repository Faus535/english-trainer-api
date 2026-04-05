package com.faus535.englishtrainer.talk.infrastructure;

import com.faus535.englishtrainer.talk.domain.*;

import java.util.*;

public class InMemoryTalkConversationRepository implements TalkConversationRepository {

    private final Map<UUID, TalkConversation> store = new LinkedHashMap<>();

    @Override
    public TalkConversation save(TalkConversation conversation) {
        store.put(conversation.id().value(), conversation);
        return conversation;
    }

    @Override
    public Optional<TalkConversation> findById(TalkConversationId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<TalkConversation> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(c -> c.userId().equals(userId))
                .sorted(Comparator.comparing(TalkConversation::startedAt).reversed())
                .toList();
    }

    @Override
    public int countActiveByUserId(UUID userId) {
        return (int) store.values().stream()
                .filter(c -> c.userId().equals(userId))
                .filter(c -> c.status() == TalkStatus.ACTIVE)
                .count();
    }
}
