package com.faus535.englishtrainer.article.infrastructure;

import com.faus535.englishtrainer.article.domain.ArticleQuestion;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import com.faus535.englishtrainer.article.domain.ArticleQuestionRepository;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;

import java.util.*;

public class InMemoryArticleQuestionRepository implements ArticleQuestionRepository {

    private final Map<UUID, ArticleQuestion> store = new LinkedHashMap<>();

    @Override
    public void save(ArticleQuestion question) {
        store.put(question.id().value(), question);
    }

    @Override
    public List<ArticleQuestion> findByArticleReadingId(ArticleReadingId articleReadingId) {
        return store.values().stream()
                .filter(q -> q.articleReadingId().equals(articleReadingId))
                .sorted(Comparator.comparingInt(ArticleQuestion::orderIndex))
                .toList();
    }

    @Override
    public Optional<ArticleQuestion> findById(ArticleQuestionId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    public int count() { return store.size(); }
}
