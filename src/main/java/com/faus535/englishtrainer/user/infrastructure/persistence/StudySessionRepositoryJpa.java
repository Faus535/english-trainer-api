package com.faus535.englishtrainer.user.infrastructure.persistence;

import com.faus535.englishtrainer.user.domain.StudySession;
import com.faus535.englishtrainer.user.domain.StudySessionRepository;
import org.springframework.stereotype.Repository;

@Repository
class StudySessionRepositoryJpa implements StudySessionRepository {

    private final JpaStudySessionRepository jpaRepository;

    StudySessionRepositoryJpa(JpaStudySessionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(StudySession session) {
        jpaRepository.saveAndFlush(StudySessionEntity.fromAggregate(session));
    }
}
