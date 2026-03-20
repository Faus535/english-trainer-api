package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CreateVocabEntryUseCase {

    private final VocabRepository repository;

    public CreateVocabEntryUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VocabEntry execute(String en, String ipa, String es, String type, String example, VocabLevel level) {
        VocabEntry entry = VocabEntry.create(VocabEntryId.generate(), en, ipa, es, type, example, level);
        return repository.save(entry);
    }
}
