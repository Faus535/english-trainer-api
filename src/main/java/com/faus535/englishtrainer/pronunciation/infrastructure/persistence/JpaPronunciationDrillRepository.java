package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface JpaPronunciationDrillRepository extends JpaRepository<PronunciationDrillEntity, UUID> {

    List<PronunciationDrillEntity> findByCefrLevel(String cefrLevel);

    List<PronunciationDrillEntity> findByCefrLevelAndFocus(String cefrLevel, String focus);
}
