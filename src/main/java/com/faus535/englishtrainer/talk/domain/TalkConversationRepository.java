package com.faus535.englishtrainer.talk.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TalkConversationRepository {

    TalkConversation save(TalkConversation conversation);

    Optional<TalkConversation> findById(TalkConversationId id);

    List<TalkConversation> findByUserId(UUID userId);

    int countActiveByUserId(UUID userId);
}
