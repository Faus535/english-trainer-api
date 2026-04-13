package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrill;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class ListDrillsUseCase {

    private final PronunciationDrillRepository drillRepository;

    public ListDrillsUseCase(PronunciationDrillRepository drillRepository) {
        this.drillRepository = drillRepository;
    }

    @Transactional(readOnly = true)
    public List<PronunciationDrillDto> execute(String level, String focus) {
        List<PronunciationDrill> drills;
        if (focus != null && !focus.isBlank()) {
            drills = drillRepository.findByLevelAndFocus(level, focus);
        } else {
            drills = drillRepository.findByLevel(level);
        }
        return drills.stream()
                .map(d -> new PronunciationDrillDto(
                        d.id().value(), d.phrase(), d.focus(),
                        d.difficulty().name(), d.cefrLevel()))
                .toList();
    }
}
