package com.faus535.englishtrainer.talk.infrastructure.persistence;

import com.faus535.englishtrainer.talk.domain.TalkConversation;
import com.faus535.englishtrainer.talk.domain.TalkConversationId;
import com.faus535.englishtrainer.talk.domain.TalkConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaTalkConversationRepositoryAdapter implements TalkConversationRepository {

    private final JpaTalkConversationRepository jpaRepository;

    JpaTalkConversationRepositoryAdapter(JpaTalkConversationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TalkConversation save(TalkConversation conversation) {
        Optional<TalkConversationEntity> existing = jpaRepository.findById(conversation.id().value());

        if (existing.isPresent()) {
            TalkConversationEntity entity = existing.get();
            entity.updateFrom(conversation);
            return jpaRepository.save(entity).toAggregate();
        }

        TalkConversationEntity entity = TalkConversationEntity.fromAggregate(conversation);
        return jpaRepository.save(entity).toAggregate();
    }

    @Override
    public Optional<TalkConversation> findById(TalkConversationId id) {
        return jpaRepository.findById(id.value())
                .map(TalkConversationEntity::toAggregate);
    }

    @Override
    public List<TalkConversation> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(TalkConversationEntity::toAggregate)
                .toList();
    }

    @Override
    public int countActiveByUserId(UUID userId) {
        return jpaRepository.countActiveByUserId(userId);
    }
}
