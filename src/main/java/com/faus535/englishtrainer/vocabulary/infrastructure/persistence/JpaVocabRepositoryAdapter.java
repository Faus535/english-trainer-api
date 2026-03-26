package com.faus535.englishtrainer.vocabulary.infrastructure.persistence;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
class JpaVocabRepositoryAdapter implements VocabRepository {

    private final JpaVocabRepository jpaRepository;

    JpaVocabRepositoryAdapter(JpaVocabRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<VocabEntry> findAll() {
        return jpaRepository.findAll().stream()
                .map(VocabEntryEntity::toAggregate)
                .toList();
    }

    @Override
    public List<VocabEntry> findByLevel(VocabLevel level) {
        return jpaRepository.findByLevel(level.value()).stream()
                .map(VocabEntryEntity::toAggregate)
                .toList();
    }

    @Override
    public List<VocabEntry> searchByWord(String query) {
        return jpaRepository.findByEnContainingIgnoreCaseOrEsContainingIgnoreCase(query, query).stream()
                .map(VocabEntryEntity::toAggregate)
                .toList();
    }

    @Override
    public VocabEntry save(VocabEntry entry) {
        VocabEntryEntity entity = VocabEntryEntity.fromAggregate(entry);
        if (jpaRepository.existsById(entry.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }

    @Override
    public List<VocabEntry> findByLevelAndBlock(VocabLevel level, int block) {
        return jpaRepository.findByLevelAndBlock(level.value(), block).stream()
                .map(VocabEntryEntity::toAggregate)
                .toList();
    }

    @Override
    public List<VocabEntry> findRandom(int count, VocabLevel level) {
        List<VocabEntry> entries = (level != null) ? findByLevel(level) : findAll();
        List<VocabEntry> mutable = new java.util.ArrayList<>(entries);
        Collections.shuffle(mutable);
        return mutable.stream().limit(count).toList();
    }

    @Override
    public List<VocabEntry> findByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByIdIn(ids).stream()
                .map(VocabEntryEntity::toAggregate)
                .toList();
    }

    @Override
    public List<VocabEntry> findByLevelExcludingIds(VocabLevel level, Set<UUID> excludeIds, int count) {
        if (excludeIds == null || excludeIds.isEmpty()) {
            List<VocabEntry> byLevel = findByLevel(level);
            List<VocabEntry> mutable = new java.util.ArrayList<>(byLevel);
            Collections.shuffle(mutable);
            return mutable.stream().limit(count).toList();
        }
        return jpaRepository.findByLevelExcludingIds(level.value(), excludeIds).stream()
                .limit(count)
                .map(VocabEntryEntity::toAggregate)
                .toList();
    }
}
