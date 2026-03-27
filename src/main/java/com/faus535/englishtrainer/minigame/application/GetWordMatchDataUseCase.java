package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetWordMatchDataUseCase {

    private final VocabRepository vocabRepository;

    public GetWordMatchDataUseCase(VocabRepository vocabRepository) {
        this.vocabRepository = vocabRepository;
    }

    public record WordMatchPair(String en, String es, String vocabEntryId) {}

    @Transactional(readOnly = true)
    public List<WordMatchPair> execute(VocabLevel level) {
        List<VocabEntry> entries = vocabRepository.findRandom(10, level);
        return entries.stream()
                .map(entry -> new WordMatchPair(
                        entry.en(), entry.es(),
                        entry.id().value().toString()))
                .toList();
    }
}
