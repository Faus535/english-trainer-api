package com.faus535.englishtrainer.vocabulary.infrastructure;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaVocabRepository extends JpaRepository<VocabEntry, UUID> {
    List<VocabEntry> findByLevel(String level);
}
