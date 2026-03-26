package com.faus535.englishtrainer.session.infrastructure.controller;

record SessionExerciseResponse(
        int exerciseIndex,
        int blockIndex,
        String exerciseType,
        int targetCount,
        boolean completed,
        Integer correctCount,
        Integer totalCount
) {}
