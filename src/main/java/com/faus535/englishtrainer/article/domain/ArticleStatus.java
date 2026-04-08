package com.faus535.englishtrainer.article.domain;

public enum ArticleStatus {

    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    READY("READY"),
    FAILED("FAILED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED");

    private final String value;

    ArticleStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ArticleStatus fromString(String status) {
        for (ArticleStatus s : values()) {
            if (s.value.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown article status: " + status);
    }
}
