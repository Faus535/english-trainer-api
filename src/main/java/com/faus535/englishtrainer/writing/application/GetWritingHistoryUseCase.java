package com.faus535.englishtrainer.writing.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.writing.domain.WritingSubmission;
import com.faus535.englishtrainer.writing.domain.WritingSubmissionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetWritingHistoryUseCase {

    private final WritingSubmissionRepository repository;

    public GetWritingHistoryUseCase(WritingSubmissionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<WritingSubmission> execute(UUID userId) {
        return repository.findByUserId(userId);
    }
}
