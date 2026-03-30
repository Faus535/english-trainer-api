package com.faus535.englishtrainer.phonetics.infrastructure;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.*;

public final class InMemoryUserPhonemeProgressRepository implements UserPhonemeProgressRepository {

    private final Map<UUID, UserPhonemeProgress> store = new HashMap<>();

    @Override
    public Optional<UserPhonemeProgress> findByUserAndPhonemeAndPhrase(UserProfileId userId,
                                                                        PhonemeId phonemeId,
                                                                        PhonemePracticePhraseId phraseId) {
        return store.values().stream()
                .filter(p -> p.userId().equals(userId)
                        && p.phonemeId().equals(phonemeId)
                        && p.phraseId().equals(phraseId))
                .findFirst();
    }

    @Override
    public List<UserPhonemeProgress> findByUserAndPhoneme(UserProfileId userId, PhonemeId phonemeId) {
        return store.values().stream()
                .filter(p -> p.userId().equals(userId) && p.phonemeId().equals(phonemeId))
                .toList();
    }

    @Override
    public UserPhonemeProgress save(UserPhonemeProgress progress) {
        store.put(progress.id().value(), progress);
        return progress;
    }
}
