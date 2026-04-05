package com.faus535.englishtrainer.talk.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaTalkScenarioRepository extends JpaRepository<TalkScenarioEntity, UUID> {

    List<TalkScenarioEntity> findByCefrLevelOrderByDifficultyOrderAsc(String cefrLevel);

    List<TalkScenarioEntity> findAllByOrderByDifficultyOrderAsc();
}
