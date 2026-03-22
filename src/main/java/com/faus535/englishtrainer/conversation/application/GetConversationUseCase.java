package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.ConversationId;
import com.faus535.englishtrainer.conversation.domain.ConversationRepository;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GetConversationUseCase {

    private final ConversationRepository repository;

    public GetConversationUseCase(ConversationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Conversation execute(UUID conversationIdValue) throws ConversationNotFoundException {
        ConversationId id = new ConversationId(conversationIdValue);
        return repository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException(id));
    }
}
