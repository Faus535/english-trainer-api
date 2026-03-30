package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhrase;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class JpaPhonemePracticePhraseRepositoryAdapter implements PhonemePracticePhraseRepository {

    private final JpaPhonemePracticePhraseRepository jpaRepository;

    JpaPhonemePracticePhraseRepositoryAdapter(JpaPhonemePracticePhraseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<PhonemePracticePhrase> findByPhonemeId(PhonemeId phonemeId) {
        return jpaRepository.findByPhonemeId(phonemeId.value()).stream()
                .map(PhonemePracticePhraseEntity::toAggregate)
                .toList();
    }
}
