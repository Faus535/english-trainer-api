package com.faus535.englishtrainer.vocabularycontext.domain;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;

import java.util.Optional;

public interface VocabularyContextRepository {

    Optional<VocabularyContext> findByVocabularyIdAndLevel(VocabEntryId vocabularyId, String level);

    VocabularyContext save(VocabularyContext context);
}
