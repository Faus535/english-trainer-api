package com.faus535.englishtrainer.review.domain;

import java.time.LocalDate;

public record SM2Result(double newEaseFactor, int newIntervalDays, int newRepetitions, LocalDate nextReviewAt) {}
