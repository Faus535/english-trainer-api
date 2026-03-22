package com.faus535.englishtrainer.conversation.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository {

    Conversation save(Conversation conversation);

    Optional<Conversation> findById(ConversationId id);

    List<Conversation> findByUserId(UUID userId);

    int countActiveByUserId(UUID userId);
}
