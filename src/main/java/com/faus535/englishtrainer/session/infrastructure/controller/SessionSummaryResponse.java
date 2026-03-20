package com.faus535.englishtrainer.session.infrastructure.controller;

record SessionSummaryResponse(String id, String mode, String sessionType, boolean completed,
                              String startedAt, Integer durationMinutes) {
}
