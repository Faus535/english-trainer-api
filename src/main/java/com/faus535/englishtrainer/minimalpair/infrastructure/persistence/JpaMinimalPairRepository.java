package com.faus535.englishtrainer.minimalpair.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaMinimalPairRepository extends JpaRepository<MinimalPairEntity, UUID> {

    List<MinimalPairEntity> findBySoundCategoryAndLevel(String soundCategory, String level);

    List<MinimalPairEntity> findByLevel(String level);
}
