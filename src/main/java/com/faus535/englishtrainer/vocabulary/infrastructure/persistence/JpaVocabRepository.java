package com.faus535.englishtrainer.vocabulary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

interface JpaVocabRepository extends JpaRepository<VocabEntryEntity, UUID> {

    List<VocabEntryEntity> findByLevel(String level);

    List<VocabEntryEntity> findByEnContainingIgnoreCaseOrEsContainingIgnoreCase(String en, String es);

    List<VocabEntryEntity> findByLevelAndBlock(String level, int block);

    List<VocabEntryEntity> findByIdIn(Collection<UUID> ids);

    @Query("SELECT v FROM VocabEntryEntity v WHERE v.level = :level AND v.id NOT IN :excludeIds ORDER BY FUNCTION('RANDOM')")
    List<VocabEntryEntity> findByLevelExcludingIds(@Param("level") String level,
                                                     @Param("excludeIds") Collection<UUID> excludeIds);
}
