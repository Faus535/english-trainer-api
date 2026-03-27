package com.faus535.englishtrainer.session.infrastructure.controller;

import java.util.List;

record SessionBlockResponse(
        String blockType,
        String moduleName,
        int durationMinutes,
        int exerciseCount,
        int completedExercises,
        boolean blockCompleted,
        List<SessionExerciseResponse> exercises
) {}
