package com.faus535.englishtrainer.minimalpair.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface JpaMinimalPairResultRepository extends JpaRepository<MinimalPairResultEntity, UUID> {

    List<MinimalPairResultEntity> findByUserId(UUID userId);

    @Query(value = """
            SELECT mp.sound_category AS soundCategory,
                   COUNT(*) AS total,
                   SUM(CASE WHEN r.correct = true THEN 1 ELSE 0 END) AS correct
            FROM minimal_pair_results r
            JOIN minimal_pairs mp ON mp.id = r.pair_id
            WHERE r.user_id = :userId
            GROUP BY mp.sound_category
            """, nativeQuery = true)
    List<CategoryAccuracyProjection> findAccuracyByUserId(@Param("userId") UUID userId);

    interface CategoryAccuracyProjection {
        String getSoundCategory();
        Long getTotal();
        Long getCorrect();
    }
}
