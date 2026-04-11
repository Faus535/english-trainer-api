package com.faus535.englishtrainer.immerse.domain;

import java.time.Instant;
import java.util.UUID;

public interface ImmerseSubmissionRepository {

    ImmerseSubmission save(ImmerseSubmission submission);

    long countByUserIdAndCreatedAtAfter(UUID userId, Instant since);
}
