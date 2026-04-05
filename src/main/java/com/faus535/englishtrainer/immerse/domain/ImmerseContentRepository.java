package com.faus535.englishtrainer.immerse.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImmerseContentRepository {

    ImmerseContent save(ImmerseContent content);

    Optional<ImmerseContent> findById(ImmerseContentId id);

    List<ImmerseContent> findByUserId(UUID userId, int page, int size);
}
