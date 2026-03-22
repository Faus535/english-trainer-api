package com.faus535.englishtrainer.reading.domain;

import java.util.List;
import java.util.UUID;

public interface ReadingSubmissionRepository {

    ReadingSubmission save(ReadingSubmission submission);

    List<ReadingSubmission> findByUserId(UUID userId);
}
