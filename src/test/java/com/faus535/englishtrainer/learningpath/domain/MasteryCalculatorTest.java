package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.session.domain.ExerciseResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MasteryCalculatorTest {

    @Test
    void should_calculate_weighted_mastery_score() {
        List<ExerciseResult> results = List.of(
                new ExerciseResult(8, 10, 500, Instant.now()),  // VOCAB_QUIZ: 80%
                new ExerciseResult(6, 10, 600, Instant.now()),  // LISTENING: 60%
                new ExerciseResult(7, 10, 400, Instant.now()),  // PRONUNCIATION: 70%
                new ExerciseResult(9, 10, 300, Instant.now())   // GRAMMAR: 90%
        );
        List<String> types = List.of("VOCAB_QUIZ", "LISTENING", "PRONUNCIATION", "GRAMMAR");

        MasteryScore score = MasteryCalculator.calculate(results, types);

        // Weighted: (80*0.40 + 60*0.25 + 70*0.20 + 90*0.15) / (0.40+0.25+0.20+0.15)
        // = (32 + 15 + 14 + 13.5) / 1.0 = 74.5 -> 75
        assertEquals(75, score.value());
    }

    @Test
    void should_calculate_score_for_single_exercise_type() {
        List<ExerciseResult> results = List.of(
                new ExerciseResult(7, 10, 500, Instant.now()),
                new ExerciseResult(9, 10, 400, Instant.now())
        );
        List<String> types = List.of("VOCAB_QUIZ", "VOCAB_QUIZ");

        MasteryScore score = MasteryCalculator.calculate(results, types);

        // Average of 70% and 90% = 80%
        assertEquals(80, score.value());
    }

    @Test
    void should_return_zero_for_empty_input() {
        MasteryScore score = MasteryCalculator.calculate(List.of(), List.of());

        assertEquals(0, score.value());
    }

    @Test
    void should_handle_unknown_exercise_type_with_default_weight() {
        List<ExerciseResult> results = List.of(
                new ExerciseResult(8, 10, 500, Instant.now())
        );
        List<String> types = List.of("UNKNOWN_TYPE");

        MasteryScore score = MasteryCalculator.calculate(results, types);

        assertEquals(80, score.value());
    }

    @Test
    void should_handle_zero_total_count() {
        List<ExerciseResult> results = List.of(
                new ExerciseResult(0, 0, 0, Instant.now())
        );
        List<String> types = List.of("VOCAB_QUIZ");

        MasteryScore score = MasteryCalculator.calculate(results, types);

        assertEquals(0, score.value());
    }
}
