package com.faus535.englishtrainer.pronunciation.domain;

import java.util.Optional;

public interface PronunciationMiniConversationRepository {

    Optional<PronunciationMiniConversation> findById(PronunciationMiniConversationId id);

    PronunciationMiniConversation save(PronunciationMiniConversation conversation);
}
