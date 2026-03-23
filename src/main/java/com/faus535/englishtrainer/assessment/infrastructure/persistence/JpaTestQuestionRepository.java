package com.faus535.englishtrainer.assessment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface JpaTestQuestionRepository extends JpaRepository<TestQuestionEntity, UUID> {

    @Query(value = "SELECT * FROM test_questions WHERE type = :type AND level = :level AND active = true ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<TestQuestionEntity> findRandomByTypeAndLevel(String type, String level, int count);

    @Query(value = "SELECT * FROM test_questions WHERE type = :type AND level = :level AND active = true AND id NOT IN :excludeIds ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<TestQuestionEntity> findRandomByTypeAndLevelExcluding(String type, String level, int count, List<UUID> excludeIds);
}
