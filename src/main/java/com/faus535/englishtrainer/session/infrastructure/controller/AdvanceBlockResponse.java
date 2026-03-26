package com.faus535.englishtrainer.session.infrastructure.controller;

record AdvanceBlockResponse(
        int blockIndex,
        boolean blockCompleted,
        Integer nextBlockIndex,
        boolean sessionCompleted,
        int completedExercises,
        int totalExercises
) {}
