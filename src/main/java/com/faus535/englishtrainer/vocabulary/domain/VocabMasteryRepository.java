package com.faus535.englishtrainer.vocabulary.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Optional;

public interface VocabMasteryRepository {

    Optional<VocabMastery> findByUserIdAndVocabEntryId(UserProfileId userId, VocabEntryId vocabEntryId);

    Optional<VocabMastery> findByUserIdAndWord(UserProfileId userId, String word);

    List<VocabMastery> findByUserId(UserProfileId userId);

    List<VocabMastery> findByUserIdAndStatus(UserProfileId userId, VocabMasteryStatus status);

    VocabMastery save(VocabMastery mastery);
}
