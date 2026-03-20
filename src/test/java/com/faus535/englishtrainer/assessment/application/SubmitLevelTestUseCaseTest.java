package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.assessment.infrastructure.InMemoryLevelTestResultRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

final class SubmitLevelTestUseCaseTest {

    private InMemoryUserProfileRepository userProfileRepository;
    private InMemoryLevelTestResultRepository levelTestResultRepository;
    private SubmitLevelTestUseCase useCase;

    @BeforeEach
    void setUp() {
        userProfileRepository = new InMemoryUserProfileRepository();
        levelTestResultRepository = new InMemoryLevelTestResultRepository();
        GetLevelTestQuestionsUseCase getLevelTestQuestionsUseCase = new GetLevelTestQuestionsUseCase();
        ApplicationEventPublisher publisher = event -> {};
        useCase = new SubmitLevelTestUseCase(userProfileRepository, levelTestResultRepository,
                getLevelTestQuestionsUseCase, publisher);
    }

    @Test
    void shouldCreateLevelTestResult() throws UserProfileNotFoundException, InvalidModuleException {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);

        GetLevelTestQuestionsUseCase questionsUseCase = new GetLevelTestQuestionsUseCase();
        List<TestQuestion> questions = questionsUseCase.execute();

        Map<String, String> answers = new HashMap<>();
        for (TestQuestion q : questions) {
            answers.put(q.id(), q.correctAnswer());
        }

        LevelTestResult result = useCase.execute(profile.id(), answers);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(profile.id(), result.userId());

        UserProfile updatedProfile = userProfileRepository.findById(profile.id()).orElseThrow();
        assertTrue(updatedProfile.testCompleted());
    }
}
