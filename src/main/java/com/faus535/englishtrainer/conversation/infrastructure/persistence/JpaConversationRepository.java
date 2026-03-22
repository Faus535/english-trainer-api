package com.faus535.englishtrainer.conversation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface JpaConversationRepository extends JpaRepository<ConversationEntity, UUID> {

    List<ConversationEntity> findByUserIdOrderByStartedAtDesc(UUID userId);

    @Query("SELECT COUNT(c) FROM ConversationEntity c WHERE c.userId = :userId AND c.status = 'active'")
    int countActiveByUserId(UUID userId);
}
