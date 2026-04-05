package com.faus535.englishtrainer.immerse.infrastructure;

import com.faus535.englishtrainer.immerse.domain.ImmerseSubmission;
import com.faus535.englishtrainer.immerse.domain.ImmerseSubmissionRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryImmerseSubmissionRepository implements ImmerseSubmissionRepository {

    private final List<ImmerseSubmission> store = new ArrayList<>();

    @Override
    public ImmerseSubmission save(ImmerseSubmission submission) {
        store.add(submission);
        return submission;
    }

    public List<ImmerseSubmission> findAll() { return List.copyOf(store); }
}
