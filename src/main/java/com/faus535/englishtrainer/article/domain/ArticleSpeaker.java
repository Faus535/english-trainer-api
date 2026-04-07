package com.faus535.englishtrainer.article.domain;

public enum ArticleSpeaker {

    AI, USER;

    public static ArticleSpeaker fromString(String speaker) {
        for (ArticleSpeaker s : values()) {
            if (s.name().equalsIgnoreCase(speaker)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown article speaker: " + speaker);
    }
}
