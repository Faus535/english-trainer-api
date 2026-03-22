package com.faus535.englishtrainer.reading.domain;

import java.util.List;
import java.util.Optional;

public interface ReadingPassageRepository {

    List<ReadingPassage> findByLevel(String level);

    Optional<ReadingPassage> findById(ReadingPassageId id);

    ReadingPassage save(ReadingPassage passage);
}
