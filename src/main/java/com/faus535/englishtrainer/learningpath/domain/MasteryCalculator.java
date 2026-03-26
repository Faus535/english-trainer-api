package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.session.domain.ExerciseResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MasteryCalculator {

    private static final Map<String, Double> WEIGHTS = Map.of(
            "VOCAB_QUIZ", 0.40,
            "LISTENING", 0.25,
            "PRONUNCIATION", 0.20,
            "GRAMMAR", 0.15
    );

    private MasteryCalculator() {}

    public static MasteryScore calculate(List<ExerciseResult> results, List<String> exerciseTypes) {
        if (results.isEmpty() || exerciseTypes.isEmpty()) {
            return new MasteryScore(0);
        }

        if (results.size() != exerciseTypes.size()) {
            throw new IllegalArgumentException(
                    "results and exerciseTypes must have the same size");
        }

        Map<String, List<Integer>> resultsByType = groupResultsByType(results, exerciseTypes);

        double totalWeight = 0.0;
        double weightedSum = 0.0;

        for (Map.Entry<String, List<Integer>> entry : resultsByType.entrySet()) {
            String type = entry.getKey();
            List<Integer> scores = entry.getValue();

            double weight = WEIGHTS.getOrDefault(type, 0.10);
            double average = scores.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            weightedSum += average * weight;
            totalWeight += weight;
        }

        if (totalWeight == 0.0) {
            return new MasteryScore(0);
        }

        int score = (int) Math.round(weightedSum / totalWeight);
        score = Math.max(0, Math.min(100, score));

        return new MasteryScore(score);
    }

    private static Map<String, List<Integer>> groupResultsByType(
            List<ExerciseResult> results, List<String> exerciseTypes) {

        return java.util.stream.IntStream.range(0, results.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        exerciseTypes::get,
                        Collectors.mapping(
                                i -> scoreFromResult(results.get(i)),
                                Collectors.toList()
                        )
                ));
    }

    private static int scoreFromResult(ExerciseResult result) {
        if (result.totalCount() == 0) {
            return 0;
        }
        return (int) Math.round((double) result.correctCount() / result.totalCount() * 100);
    }
}
