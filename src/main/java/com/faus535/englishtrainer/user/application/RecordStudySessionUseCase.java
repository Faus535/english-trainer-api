package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.StudySession;
import com.faus535.englishtrainer.user.domain.StudySessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.domain.vo.StudyModule;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class RecordStudySessionUseCase {

    private final UserProfileRepository userProfileRepository;
    private final StudySessionRepository studySessionRepository;

    public RecordStudySessionUseCase(UserProfileRepository userProfileRepository,
                                     StudySessionRepository studySessionRepository) {
        this.userProfileRepository = userProfileRepository;
        this.studySessionRepository = studySessionRepository;
    }

    @Transactional
    public void execute(UUID userId, StudyModule module, int durationSeconds) throws UserProfileNotFoundException {
        UserProfileId profileId = new UserProfileId(userId);
        userProfileRepository.findById(profileId)
                .orElseThrow(() -> new UserProfileNotFoundException(profileId));
        StudySession session = StudySession.create(userId, module, durationSeconds);
        studySessionRepository.save(session);
    }
}
