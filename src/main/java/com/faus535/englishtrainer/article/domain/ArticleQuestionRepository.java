package com.faus535.englishtrainer.article.domain;

import java.util.List;
import java.util.Optional;

public interface ArticleQuestionRepository {

    void save(ArticleQuestion question);

    List<ArticleQuestion> findByArticleReadingId(ArticleReadingId articleReadingId);

    Optional<ArticleQuestion> findById(ArticleQuestionId id);
}
