package com.faus535.englishtrainer.reading.infrastructure.persistence;

import com.faus535.englishtrainer.reading.domain.ReadingSubmission;
import com.faus535.englishtrainer.reading.domain.ReadingSubmissionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class JpaReadingSubmissionRepositoryAdapter implements ReadingSubmissionRepository {

    private final JpaReadingSubmissionRepository jpaRepository;

    JpaReadingSubmissionRepositoryAdapter(JpaReadingSubmissionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ReadingSubmission save(ReadingSubmission submission) {
        ReadingSubmissionEntity entity = ReadingSubmissionEntity.fromAggregate(submission);
        if (jpaRepository.existsById(submission.id())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public List<ReadingSubmission> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(ReadingSubmissionEntity::toDomain).toList();
    }
}
