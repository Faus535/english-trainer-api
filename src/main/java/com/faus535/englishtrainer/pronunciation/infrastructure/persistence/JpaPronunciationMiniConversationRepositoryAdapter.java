package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversation;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationId;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaPronunciationMiniConversationRepositoryAdapter implements PronunciationMiniConversationRepository {

    private final JpaPronunciationMiniConversationRepository jpaRepository;

    JpaPronunciationMiniConversationRepositoryAdapter(
            JpaPronunciationMiniConversationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<PronunciationMiniConversation> findById(PronunciationMiniConversationId id) {
        return jpaRepository.findById(id.value())
                .map(PronunciationMiniConversationEntity::toAggregate);
    }

    @Override
    public PronunciationMiniConversation save(PronunciationMiniConversation conversation) {
        Optional<PronunciationMiniConversationEntity> existing =
                jpaRepository.findById(conversation.id().value());
        if (existing.isPresent()) {
            PronunciationMiniConversationEntity entity = existing.get();
            entity.updateFrom(conversation);
            return jpaRepository.save(entity).toAggregate();
        }
        PronunciationMiniConversationEntity entity =
                PronunciationMiniConversationEntity.fromAggregate(conversation);
        return jpaRepository.save(entity).toAggregate();
    }
}
