package com.faus535.englishtrainer.article.domain;

import java.util.Map;

public record ArticleContentSizing(
        int generateMaxTokens,
        int translateMaxTokens,
        int questionsMaxTokens,
        int correctAnswerMaxTokens
) {
    private static final Map<String, ArticleContentSizing> SIZINGS = Map.of(
            "b1", new ArticleContentSizing(1400, 200, 1050, 420),
            "b2", new ArticleContentSizing(1600, 200, 1200, 480),
            "c1", new ArticleContentSizing(1800, 200, 1350, 540)
    );

    public static ArticleContentSizing forLevel(String level) {
        if (level == null) return SIZINGS.get("b1");
        ArticleContentSizing sizing = SIZINGS.get(level.trim().toLowerCase());
        return sizing != null ? sizing : SIZINGS.get("b1");
    }
}
