package com.faus535.englishtrainer.vocabulary.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface VocabRepository {

    List<VocabEntry> findAll();

    List<VocabEntry> findByLevel(VocabLevel level);

    List<VocabEntry> searchByWord(String query);

    VocabEntry save(VocabEntry entry);

    List<VocabEntry> findRandom(int count, VocabLevel level);

    List<VocabEntry> findByLevelAndBlock(VocabLevel level, int block);

    List<VocabEntry> findByIds(List<UUID> ids);

    List<VocabEntry> findByLevelExcludingIds(VocabLevel level, Set<UUID> excludeIds, int count);
}
