package com.faus535.englishtrainer.phrase.infrastructure.persistence;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
class JpaPhraseRepositoryAdapter implements PhraseRepository {

    private final JpaPhraseRepository jpaRepository;

    JpaPhraseRepositoryAdapter(JpaPhraseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Phrase> findByLevel(VocabLevel level) {
        return jpaRepository.findByLevel(level.value()).stream()
                .map(PhraseEntity::toAggregate)
                .toList();
    }

    @Override
    public List<Phrase> findRandom(int count, VocabLevel level) {
        List<Phrase> entries = (level != null) ? findByLevel(level) : findAll();
        List<Phrase> mutable = new ArrayList<>(entries);
        Collections.shuffle(mutable);
        return mutable.stream().limit(count).toList();
    }

    @Override
    public Phrase save(Phrase phrase) {
        PhraseEntity entity = PhraseEntity.fromAggregate(phrase);
        if (jpaRepository.existsById(phrase.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }

    private List<Phrase> findAll() {
        return jpaRepository.findAll().stream()
                .map(PhraseEntity::toAggregate)
                .toList();
    }
}
