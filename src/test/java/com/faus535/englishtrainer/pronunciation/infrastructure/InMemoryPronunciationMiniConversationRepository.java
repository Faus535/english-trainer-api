package com.faus535.englishtrainer.pronunciation.infrastructure;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversation;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationId;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPronunciationMiniConversationRepository
        implements PronunciationMiniConversationRepository {

    private final Map<PronunciationMiniConversationId, PronunciationMiniConversation> store = new HashMap<>();

    public void add(PronunciationMiniConversation conversation) {
        store.put(conversation.id(), conversation);
    }

    public void clear() {
        store.clear();
    }

    public int count() {
        return store.size();
    }

    @Override
    public Optional<PronunciationMiniConversation> findById(PronunciationMiniConversationId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public PronunciationMiniConversation save(PronunciationMiniConversation conversation) {
        store.put(conversation.id(), conversation);
        return conversation;
    }
}
