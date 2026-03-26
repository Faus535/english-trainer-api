package com.faus535.englishtrainer.vocabulary.infrastructure;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;

import java.util.*;

public final class InMemoryVocabMasteryRepository implements VocabMasteryRepository {

    private final Map<UUID, VocabMastery> store = new HashMap<>();

    @Override
    public Optional<VocabMastery> findByUserIdAndVocabEntryId(UserProfileId userId, VocabEntryId vocabEntryId) {
        return store.values().stream()
                .filter(m -> m.userId().equals(userId) && vocabEntryId != null
                        && m.vocabEntryId() != null && m.vocabEntryId().equals(vocabEntryId))
                .findFirst();
    }

    @Override
    public Optional<VocabMastery> findByUserIdAndWord(UserProfileId userId, String word) {
        return store.values().stream()
                .filter(m -> m.userId().equals(userId) && m.word().equals(word))
                .findFirst();
    }

    @Override
    public List<VocabMastery> findByUserId(UserProfileId userId) {
        return store.values().stream()
                .filter(m -> m.userId().equals(userId))
                .toList();
    }

    @Override
    public List<VocabMastery> findByUserIdAndStatus(UserProfileId userId, VocabMasteryStatus status) {
        return store.values().stream()
                .filter(m -> m.userId().equals(userId) && m.status() == status)
                .toList();
    }

    @Override
    public VocabMastery save(VocabMastery mastery) {
        store.put(mastery.id().value(), mastery);
        return mastery;
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
