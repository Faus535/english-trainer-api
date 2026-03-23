package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.assessment.infrastructure.InMemoryLevelTestResultRepository;
import com.faus535.englishtrainer.assessment.infrastructure.InMemoryTestQuestionHistoryRepository;
import com.faus535.englishtrainer.assessment.infrastructure.InMemoryTestQuestionRepository;
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
    private InMemoryTestQuestionRepository testQuestionRepository;
    private InMemoryTestQuestionHistoryRepository historyRepository;
    private SubmitLevelTestUseCase useCase;
    private GetLevelTestQuestionsUseCase getLevelTestQuestionsUseCase;

    @BeforeEach
    void setUp() {
        userProfileRepository = new InMemoryUserProfileRepository();
        levelTestResultRepository = new InMemoryLevelTestResultRepository();
        testQuestionRepository = new InMemoryTestQuestionRepository();
        historyRepository = new InMemoryTestQuestionHistoryRepository();
        getLevelTestQuestionsUseCase = new GetLevelTestQuestionsUseCase(testQuestionRepository, historyRepository);
        ApplicationEventPublisher publisher = event -> {};
        useCase = new SubmitLevelTestUseCase(userProfileRepository, levelTestResultRepository,
                getLevelTestQuestionsUseCase, historyRepository, publisher);

        seedQuestions();
    }

    @Test
    void shouldCreateLevelTestResult() throws UserProfileNotFoundException, InvalidModuleException, TestCooldownException {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);

        List<TestQuestion> questions = getLevelTestQuestionsUseCase.execute(profile.id());

        Map<String, String> answers = new HashMap<>();
        for (TestQuestion q : questions) {
            answers.put(q.id().toString(), q.correctAnswer());
        }

        SubmitLevelTestUseCase.SubmitResult result = useCase.execute(profile.id(), answers);

        assertNotNull(result.testResult());
        assertNotNull(result.testResult().id());
        assertEquals(profile.id(), result.testResult().userId());

        UserProfile updatedProfile = userProfileRepository.findById(profile.id()).orElseThrow();
        assertTrue(updatedProfile.testCompleted());
    }

    private void seedQuestions() {
        testQuestionRepository.addAll(List.of(
                // Vocabulary
                new TestQuestion("v1", "vocabulary", "What does 'hello' mean?", List.of("hola", "adiós"), "hola", "a1"),
                new TestQuestion("v2", "vocabulary", "What does 'water' mean?", List.of("fuego", "agua"), "agua", "a1"),
                new TestQuestion("v3", "vocabulary", "What does 'book' mean?", List.of("mesa", "libro"), "libro", "a1"),
                new TestQuestion("v4", "vocabulary", "What does 'cat' mean?", List.of("perro", "gato"), "gato", "a1"),
                new TestQuestion("v5", "vocabulary", "What does 'house' mean?", List.of("coche", "casa"), "casa", "a1"),
                new TestQuestion("v6", "vocabulary", "What does 'schedule' mean?", List.of("horario", "escuela"), "horario", "a2"),
                new TestQuestion("v7", "vocabulary", "What does 'improve' mean?", List.of("empeorar", "mejorar"), "mejorar", "a2"),
                new TestQuestion("v8", "vocabulary", "What does 'appointment' mean?", List.of("apartamento", "cita"), "cita", "a2"),
                new TestQuestion("v9", "vocabulary", "What does 'reliable' mean?", List.of("confiable", "rápido"), "confiable", "b1"),
                new TestQuestion("v10", "vocabulary", "What does 'achieve' mean?", List.of("perder", "lograr"), "lograr", "b1"),
                new TestQuestion("v11", "vocabulary", "What does 'thoroughly' mean?", List.of("rápidamente", "minuciosamente"), "minuciosamente", "b2"),
                new TestQuestion("v12", "vocabulary", "What does 'acknowledge' mean?", List.of("ignorar", "reconocer"), "reconocer", "b2"),
                new TestQuestion("v13", "vocabulary", "What does 'ubiquitous' mean?", List.of("raro", "omnipresente"), "omnipresente", "c1"),
                new TestQuestion("v14", "vocabulary", "What does 'exacerbate' mean?", List.of("mejorar", "agravar"), "agravar", "c1"),
                new TestQuestion("v15", "vocabulary", "What does 'pragmatic' mean?", List.of("idealista", "práctico"), "práctico", "c1"),
                // Grammar
                new TestQuestion("g1", "grammar", "She ___ to school.", List.of("go", "goes"), "goes", "a1"),
                new TestQuestion("g2", "grammar", "They ___ students.", List.of("is", "are"), "are", "a1"),
                new TestQuestion("g3", "grammar", "I ___ a car.", List.of("has", "have"), "have", "a1"),
                new TestQuestion("g4", "grammar", "___ you like coffee?", List.of("Does", "Do"), "Do", "a1"),
                new TestQuestion("g5", "grammar", "He ___ not speak French.", List.of("do", "does"), "does", "a1"),
                new TestQuestion("g6", "grammar", "I ___ dinner when you called.", List.of("cook", "was cooking"), "was cooking", "a2"),
                new TestQuestion("g7", "grammar", "She has ___ to Paris twice.", List.of("be", "been"), "been", "a2"),
                new TestQuestion("g8", "grammar", "If I ___ rich, I would travel.", List.of("am", "were"), "were", "b1"),
                new TestQuestion("g9", "grammar", "The book ___ by the author.", List.of("wrote", "was written"), "was written", "b1"),
                new TestQuestion("g10", "grammar", "Had I known, I ___.", List.of("would act", "would have acted"), "would have acted", "b2"),
                new TestQuestion("g11", "grammar", "Not only ___ he pass.", List.of("does", "did"), "did", "b2"),
                new TestQuestion("g12", "grammar", "Scarcely ___ the train left.", List.of("has", "had"), "had", "c1"),
                // Listening
                new TestQuestion("l1", "listening", "Which word sounds like 'sea'?", List.of("see", "say"), "see", "a1"),
                new TestQuestion("l2", "listening", "Which word rhymes with 'cat'?", List.of("cut", "bat"), "bat", "a1"),
                new TestQuestion("l3", "listening", "Which word rhymes with 'light'?", List.of("lit", "night"), "night", "a1"),
                new TestQuestion("l4", "listening", "Which word is stressed?", List.of("I", "gone"), "gone", "a2"),
                new TestQuestion("l5", "listening", "Which pair are homophones?", List.of("their/there", "this/that"), "their/there", "a2"),
                new TestQuestion("l6", "listening", "Contraction of 'did not'?", List.of("did not", "does not"), "did not", "b1"),
                new TestQuestion("l7", "listening", "Which has a silent letter?", List.of("knight", "jump"), "knight", "b1"),
                new TestQuestion("l8", "listening", "What type of conditional?", List.of("second", "third"), "third", "b2"),
                new TestQuestion("l9", "listening", "Break the ice means?", List.of("romper algo", "iniciar una conversación"), "iniciar una conversación", "b2"),
                new TestQuestion("l10", "listening", "Bring it up means?", List.of("levantarlo", "mencionarlo"), "mencionarlo", "c1"),
                // Pronunciation
                new TestQuestion("p1", "pronunciation", "Syllables in 'beautiful'?", List.of("2", "3"), "3", "a1"),
                new TestQuestion("p2", "pronunciation", "Which starts with vowel sound?", List.of("house", "hour"), "hour", "a1"),
                new TestQuestion("p3", "pronunciation", "Syllables in 'interesting'?", List.of("3", "4"), "4", "a1"),
                new TestQuestion("p4", "pronunciation", "Stress in 'computer'?", List.of("COM-pu-ter", "com-PU-ter"), "com-PU-ter", "a2"),
                new TestQuestion("p5", "pronunciation", "Different vowel sound?", List.of("food", "good"), "good", "a2"),
                new TestQuestion("p6", "pronunciation", "Stress in 'photograph'?", List.of("PHO-to-graph vs pho-TO-gra-phy", "Same"), "PHO-to-graph vs pho-TO-gra-phy", "b1"),
                new TestQuestion("p7", "pronunciation", "Comfortable syllables?", List.of("3", "4"), "3", "b1"),
                new TestQuestion("p8", "pronunciation", "Type of stress on SAID?", List.of("word stress", "contrastive stress"), "contrastive stress", "b2"),
                new TestQuestion("p9", "pronunciation", "She didn't STEAL it uses:", List.of("emphasis", "correction"), "correction", "c1")
        ));
    }
}
