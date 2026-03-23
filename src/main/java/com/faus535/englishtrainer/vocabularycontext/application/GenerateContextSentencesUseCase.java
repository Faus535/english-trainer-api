package com.faus535.englishtrainer.vocabularycontext.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabularycontext.domain.ContextGeneratorPort;
import com.faus535.englishtrainer.vocabularycontext.domain.VocabularyContext;
import com.faus535.englishtrainer.vocabularycontext.domain.VocabularyContextRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@UseCase
public class GenerateContextSentencesUseCase {

    private final VocabularyContextRepository repository;
    private final ContextGeneratorPort contextGenerator;

    public GenerateContextSentencesUseCase(VocabularyContextRepository repository,
                                            ContextGeneratorPort contextGenerator) {
        this.repository = repository;
        this.contextGenerator = contextGenerator;
    }

    @Transactional
    public VocabularyContext execute(VocabEntryId vocabularyId, String word, String level) throws Exception {
        Optional<VocabularyContext> cached = repository.findByVocabularyIdAndLevel(vocabularyId, level);
        if (cached.isPresent()) {
            return cached.get();
        }

        String sentencesJson = contextGenerator.generateSentences(word, level);
        VocabularyContext context = VocabularyContext.create(vocabularyId, level, sentencesJson);
        return repository.save(context);
    }
}
