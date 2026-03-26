package com.faus535.englishtrainer.session.domain;

public record BlockProgress(int blockIndex, int totalExercises, int completedExercises) {

    public boolean isCompleted() {
        return completedExercises >= totalExercises;
    }
}
