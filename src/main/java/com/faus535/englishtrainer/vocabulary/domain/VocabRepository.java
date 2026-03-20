package com.faus535.englishtrainer.vocabulary.domain;

import java.util.List;
import java.util.UUID;

public interface VocabRepository {
    List<VocabEntry> findAll();
    List<VocabEntry> findByLevel(String level);
    VocabEntry findById(UUID id);
    VocabEntry save(VocabEntry entry);
    long count();
}
