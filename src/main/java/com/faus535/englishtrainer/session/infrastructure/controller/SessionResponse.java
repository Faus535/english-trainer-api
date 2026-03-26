package com.faus535.englishtrainer.session.infrastructure.controller;

import java.util.List;

record SessionResponse(
        String id, String userId, String mode, String sessionType,
        String listeningModule, String secondaryModule, String integratorTheme,
        List<SessionBlockResponse> blocks,
        List<SessionExerciseResponse> exercises,
        boolean completed,
        String startedAt, String completedAt, Integer durationMinutes
) {}
