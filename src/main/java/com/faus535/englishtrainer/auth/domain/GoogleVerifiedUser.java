package com.faus535.englishtrainer.auth.domain;

public record GoogleVerifiedUser(String email, String name, boolean emailVerified) {}
