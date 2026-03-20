package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetLevelTestQuestionsUseCase {

    @Transactional(readOnly = true)
    public List<TestQuestion> execute() {
        return List.of(
                // Vocabulary A1
                new TestQuestion("v1", "vocabulary", "What does 'hello' mean?", List.of("hola", "adiós", "gracias", "por favor"), "hola", "a1"),
                new TestQuestion("v2", "vocabulary", "What does 'water' mean?", List.of("fuego", "agua", "tierra", "aire"), "agua", "a1"),
                new TestQuestion("v3", "vocabulary", "What does 'book' mean?", List.of("mesa", "silla", "libro", "puerta"), "libro", "a1"),
                new TestQuestion("v4", "vocabulary", "What does 'cat' mean?", List.of("perro", "gato", "pájaro", "pez"), "gato", "a1"),
                new TestQuestion("v5", "vocabulary", "What does 'house' mean?", List.of("coche", "casa", "calle", "ciudad"), "casa", "a1"),
                // Vocabulary A2
                new TestQuestion("v6", "vocabulary", "What does 'schedule' mean?", List.of("horario", "escuela", "clase", "tarea"), "horario", "a2"),
                new TestQuestion("v7", "vocabulary", "What does 'improve' mean?", List.of("empeorar", "mejorar", "mantener", "cambiar"), "mejorar", "a2"),
                new TestQuestion("v8", "vocabulary", "What does 'appointment' mean?", List.of("apartamento", "cita", "departamento", "documento"), "cita", "a2"),
                // Vocabulary B1
                new TestQuestion("v9", "vocabulary", "What does 'reliable' mean?", List.of("confiable", "rápido", "lento", "caro"), "confiable", "b1"),
                new TestQuestion("v10", "vocabulary", "What does 'achieve' mean?", List.of("perder", "lograr", "intentar", "fallar"), "lograr", "b1"),
                // Vocabulary B2
                new TestQuestion("v11", "vocabulary", "What does 'thoroughly' mean?", List.of("rápidamente", "minuciosamente", "fácilmente", "brevemente"), "minuciosamente", "b2"),
                new TestQuestion("v12", "vocabulary", "What does 'acknowledge' mean?", List.of("ignorar", "reconocer", "negar", "olvidar"), "reconocer", "b2"),
                // Vocabulary C1
                new TestQuestion("v13", "vocabulary", "What does 'ubiquitous' mean?", List.of("raro", "omnipresente", "invisible", "antiguo"), "omnipresente", "c1"),
                new TestQuestion("v14", "vocabulary", "What does 'exacerbate' mean?", List.of("mejorar", "agravar", "resolver", "simplificar"), "agravar", "c1"),
                new TestQuestion("v15", "vocabulary", "What does 'pragmatic' mean?", List.of("idealista", "teórico", "práctico", "abstracto"), "práctico", "c1"),

                // Grammar A1
                new TestQuestion("g1", "grammar", "She ___ to school every day.", List.of("go", "goes", "going", "gone"), "goes", "a1"),
                new TestQuestion("g2", "grammar", "They ___ students.", List.of("is", "am", "are", "be"), "are", "a1"),
                new TestQuestion("g3", "grammar", "I ___ a car.", List.of("has", "have", "having", "had"), "have", "a1"),
                new TestQuestion("g4", "grammar", "___ you like coffee?", List.of("Does", "Do", "Is", "Are"), "Do", "a1"),
                new TestQuestion("g5", "grammar", "He ___ not speak French.", List.of("do", "does", "is", "are"), "does", "a1"),
                // Grammar A2
                new TestQuestion("g6", "grammar", "I ___ dinner when you called.", List.of("cook", "cooked", "was cooking", "am cooking"), "was cooking", "a2"),
                new TestQuestion("g7", "grammar", "She has ___ to Paris twice.", List.of("be", "been", "being", "was"), "been", "a2"),
                // Grammar B1
                new TestQuestion("g8", "grammar", "If I ___ rich, I would travel the world.", List.of("am", "was", "were", "be"), "were", "b1"),
                new TestQuestion("g9", "grammar", "The book ___ by the author last year.", List.of("wrote", "was written", "has written", "writes"), "was written", "b1"),
                // Grammar B2
                new TestQuestion("g10", "grammar", "Had I known, I ___ differently.", List.of("would act", "would have acted", "acted", "will act"), "would have acted", "b2"),
                new TestQuestion("g11", "grammar", "Not only ___ he pass the exam, but he got the highest score.", List.of("does", "did", "has", "was"), "did", "b2"),
                // Grammar C1
                new TestQuestion("g12", "grammar", "Scarcely ___ the train left when it started raining.", List.of("has", "had", "have", "did"), "had", "c1"),

                // Listening A1
                new TestQuestion("l1", "listening", "Which word sounds like 'sea'?", List.of("see", "say", "so", "sue"), "see", "a1"),
                new TestQuestion("l2", "listening", "Which word rhymes with 'cat'?", List.of("cut", "bat", "cot", "bit"), "bat", "a1"),
                new TestQuestion("l3", "listening", "Which word rhymes with 'light'?", List.of("lit", "let", "night", "lot"), "night", "a1"),
                // Listening A2
                new TestQuestion("l4", "listening", "'I would have gone' - which word is stressed?", List.of("I", "would", "have", "gone"), "gone", "a2"),
                new TestQuestion("l5", "listening", "Which pair are homophones?", List.of("their/there", "this/that", "here/hear", "both A and C"), "both A and C", "a2"),
                // Listening B1
                new TestQuestion("l6", "listening", "In the sentence 'She didn't go', what is the contraction of?", List.of("did not", "does not", "do not", "is not"), "did not", "b1"),
                new TestQuestion("l7", "listening", "Which word has a silent letter: knight, jump, plan, desk?", List.of("knight", "jump", "plan", "desk"), "knight", "b1"),

                // Pronunciation A1
                new TestQuestion("p1", "pronunciation", "How many syllables does 'beautiful' have?", List.of("2", "3", "4", "5"), "3", "a1"),
                new TestQuestion("p2", "pronunciation", "Which word starts with a vowel sound?", List.of("house", "hour", "horse", "hat"), "hour", "a1"),
                new TestQuestion("p3", "pronunciation", "How many syllables does 'interesting' have?", List.of("2", "3", "4", "5"), "4", "a1"),
                // Pronunciation A2
                new TestQuestion("p4", "pronunciation", "Where is the stress in 'computer'?", List.of("COM-pu-ter", "com-PU-ter", "com-pu-TER", "all equal"), "com-PU-ter", "a2"),
                new TestQuestion("p5", "pronunciation", "Which word has a different vowel sound: 'boot', 'food', 'good', 'moon'?", List.of("boot", "food", "good", "moon"), "good", "a2")
        );
    }
}
