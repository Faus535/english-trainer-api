package com.faus535.englishtrainer.immerse.infrastructure.persistence;

import com.faus535.englishtrainer.immerse.domain.ImmerseSubmission;
import com.faus535.englishtrainer.immerse.domain.ImmerseSubmissionRepository;
import org.springframework.stereotype.Repository;

@Repository
class JpaImmerseSubmissionRepositoryAdapter implements ImmerseSubmissionRepository {

    private final JpaImmerseSubmissionRepository jpaRepository;

    JpaImmerseSubmissionRepositoryAdapter(JpaImmerseSubmissionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ImmerseSubmission save(ImmerseSubmission submission) {
        jpaRepository.save(ImmerseSubmissionEntity.fromDomain(submission));
        return submission;
    }
}
