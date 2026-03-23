package com.faus535.englishtrainer.vocabulary.domain;

import java.util.List;

public interface VocabRepository {

    List<VocabEntry> findAll();

    List<VocabEntry> findByLevel(VocabLevel level);

    List<VocabEntry> searchByWord(String query);

    VocabEntry save(VocabEntry entry);

    List<VocabEntry> findRandom(int count, VocabLevel level);

    List<VocabEntry> findByLevelAndBlock(VocabLevel level, int block);
}
