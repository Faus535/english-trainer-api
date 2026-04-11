package com.faus535.englishtrainer.immerse.infrastructure;

import com.faus535.englishtrainer.immerse.domain.ImmerseSubmission;
import com.faus535.englishtrainer.immerse.domain.ImmerseSubmissionRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryImmerseSubmissionRepository implements ImmerseSubmissionRepository {

    private final List<ImmerseSubmission> store = new ArrayList<>();

    @Override
    public ImmerseSubmission save(ImmerseSubmission submission) {
        store.add(submission);
        return submission;
    }

    @Override
    public long countByUserIdAndCreatedAtAfter(UUID userId, Instant since) {
        return store.stream()
                .filter(s -> s.userId().equals(userId) && s.submittedAt().isAfter(since))
                .count();
    }

    public List<ImmerseSubmission> findAll() { return List.copyOf(store); }
}
