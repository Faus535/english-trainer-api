package com.faus535.englishtrainer.conversation.infrastructure.persistence;

import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.ConversationId;
import com.faus535.englishtrainer.conversation.domain.ConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaConversationRepositoryAdapter implements ConversationRepository {

    private final JpaConversationRepository jpaRepository;

    JpaConversationRepositoryAdapter(JpaConversationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Conversation save(Conversation conversation) {
        Optional<ConversationEntity> existing = jpaRepository.findById(conversation.id().value());

        if (existing.isPresent()) {
            ConversationEntity entity = existing.get();
            entity.updateFrom(conversation);
            return jpaRepository.save(entity).toAggregate();
        }

        ConversationEntity entity = ConversationEntity.fromAggregate(conversation);
        return jpaRepository.save(entity).toAggregate();
    }

    @Override
    public Optional<Conversation> findById(ConversationId id) {
        return jpaRepository.findById(id.value())
                .map(ConversationEntity::toAggregate);
    }

    @Override
    public List<Conversation> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(ConversationEntity::toAggregate)
                .toList();
    }

    @Override
    public int countActiveByUserId(UUID userId) {
        return jpaRepository.countActiveByUserId(userId);
    }
}
