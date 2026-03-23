package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetFillGapDataUseCase {

    private final PhraseRepository phraseRepository;

    public GetFillGapDataUseCase(PhraseRepository phraseRepository) {
        this.phraseRepository = phraseRepository;
    }

    public record FillGapItem(String en, String es) {}

    @Transactional(readOnly = true)
    public List<FillGapItem> execute(VocabLevel level) {
        List<Phrase> phrases = phraseRepository.findRandom(8, level);
        return phrases.stream()
                .map(phrase -> new FillGapItem(phrase.en(), phrase.es()))
                .toList();
    }
}
