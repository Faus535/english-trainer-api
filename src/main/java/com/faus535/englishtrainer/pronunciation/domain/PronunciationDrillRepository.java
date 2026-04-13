package com.faus535.englishtrainer.pronunciation.domain;

import java.util.List;
import java.util.Optional;

public interface PronunciationDrillRepository {

    Optional<PronunciationDrill> findById(PronunciationDrillId id);

    List<PronunciationDrill> findByLevel(String cefrLevel);

    List<PronunciationDrill> findByLevelAndFocus(String cefrLevel, String focus);

    PronunciationDrill save(PronunciationDrill drill);
}
