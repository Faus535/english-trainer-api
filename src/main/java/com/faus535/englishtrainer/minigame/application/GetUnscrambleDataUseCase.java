package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetUnscrambleDataUseCase {

    private final VocabRepository vocabRepository;

    public GetUnscrambleDataUseCase(VocabRepository vocabRepository) {
        this.vocabRepository = vocabRepository;
    }

    public record UnscrambleItem(String en, String es) {}

    @Transactional(readOnly = true)
    public List<UnscrambleItem> execute(VocabLevel level) {
        List<VocabEntry> entries = vocabRepository.findRandom(8, level);
        return entries.stream()
                .map(entry -> new UnscrambleItem(entry.en(), entry.es()))
                .toList();
    }
}
