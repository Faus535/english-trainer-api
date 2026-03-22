package com.faus535.englishtrainer.writing.infrastructure.persistence;

import com.faus535.englishtrainer.writing.domain.WritingSubmission;
import com.faus535.englishtrainer.writing.domain.WritingSubmissionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class JpaWritingSubmissionRepositoryAdapter implements WritingSubmissionRepository {

    private final JpaWritingSubmissionRepository jpaRepository;

    JpaWritingSubmissionRepositoryAdapter(JpaWritingSubmissionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WritingSubmission save(WritingSubmission submission) {
        WritingSubmissionEntity entity = WritingSubmissionEntity.fromAggregate(submission);
        if (jpaRepository.existsById(submission.id())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public List<WritingSubmission> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(WritingSubmissionEntity::toDomain).toList();
    }
}
