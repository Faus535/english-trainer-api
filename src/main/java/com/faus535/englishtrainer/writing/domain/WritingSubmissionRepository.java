package com.faus535.englishtrainer.writing.domain;

import java.util.List;
import java.util.UUID;

public interface WritingSubmissionRepository {

    WritingSubmission save(WritingSubmission submission);

    List<WritingSubmission> findByUserId(UUID userId);
}
