package com.faus535.englishtrainer.talk.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface JpaTalkConversationRepository extends JpaRepository<TalkConversationEntity, UUID> {

    List<TalkConversationEntity> findByUserIdOrderByStartedAtDesc(UUID userId);

    @Query("SELECT COUNT(c) FROM TalkConversationEntity c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    int countActiveByUserId(UUID userId);
}
