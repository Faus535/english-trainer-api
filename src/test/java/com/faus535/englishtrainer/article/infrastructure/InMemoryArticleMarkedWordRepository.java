package com.faus535.englishtrainer.article.infrastructure;

import com.faus535.englishtrainer.article.domain.ArticleMarkedWord;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordId;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;

import java.util.*;

public class InMemoryArticleMarkedWordRepository implements ArticleMarkedWordRepository {

    private final Map<UUID, ArticleMarkedWord> store = new LinkedHashMap<>();

    @Override
    public void save(ArticleMarkedWord word) throws DuplicateMarkedWordException {
        boolean duplicate = store.values().stream()
                .anyMatch(w -> w.articleReadingId().equals(word.articleReadingId())
                        && w.userId().equals(word.userId())
                        && w.wordOrPhrase().equalsIgnoreCase(word.wordOrPhrase()));
        if (duplicate) {
            throw new DuplicateMarkedWordException(word.wordOrPhrase());
        }
        store.put(word.id().value(), word);
    }

    @Override
    public List<ArticleMarkedWord> findByArticleIdAndUserId(ArticleReadingId articleReadingId, UUID userId) {
        return store.values().stream()
                .filter(w -> w.articleReadingId().equals(articleReadingId) && w.userId().equals(userId))
                .toList();
    }

    @Override
    public Optional<ArticleMarkedWord> findById(ArticleMarkedWordId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public void update(ArticleMarkedWord word) {
        store.put(word.id().value(), word);
    }

    public int count() { return store.size(); }
}
