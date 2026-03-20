package com.faus535.englishtrainer.vocabulary.infrastructure;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class VocabRepositoryAdapter implements VocabRepository {

    private final JpaVocabRepository jpa;

    public VocabRepositoryAdapter(JpaVocabRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<VocabEntry> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<VocabEntry> findByLevel(String level) {
        return jpa.findByLevel(level);
    }

    @Override
    public VocabEntry findById(UUID id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    public VocabEntry save(VocabEntry entry) {
        return jpa.save(entry);
    }

    @Override
    public long count() {
        return jpa.count();
    }
}
