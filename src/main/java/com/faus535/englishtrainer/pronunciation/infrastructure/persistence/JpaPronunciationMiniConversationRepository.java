package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface JpaPronunciationMiniConversationRepository
        extends JpaRepository<PronunciationMiniConversationEntity, UUID> {}
