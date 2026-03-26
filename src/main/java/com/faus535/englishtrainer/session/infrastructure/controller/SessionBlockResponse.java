package com.faus535.englishtrainer.session.infrastructure.controller;

record SessionBlockResponse(
        String blockType,
        String moduleName,
        int durationMinutes,
        int exerciseCount,
        int completedExercises,
        boolean blockCompleted
) {}
