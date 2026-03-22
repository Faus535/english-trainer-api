package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.ConversationRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class ListConversationsUseCase {

    private final ConversationRepository repository;

    public ListConversationsUseCase(ConversationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Conversation> execute(UUID userId) {
        return repository.findByUserId(userId);
    }
}
