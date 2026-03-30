package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Optional;

public interface UserPhonemeProgressRepository {

    Optional<UserPhonemeProgress> findByUserAndPhonemeAndPhrase(UserProfileId userId, PhonemeId phonemeId,
                                                                 PhonemePracticePhraseId phraseId);

    List<UserPhonemeProgress> findByUserAndPhoneme(UserProfileId userId, PhonemeId phonemeId);

    UserPhonemeProgress save(UserPhonemeProgress progress);
}
