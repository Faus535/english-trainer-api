package com.faus535.englishtrainer.article.infrastructure;

import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingRepository;

import java.util.*;

public class InMemoryArticleReadingRepository implements ArticleReadingRepository {

    private final Map<UUID, ArticleReading> store = new LinkedHashMap<>();

    @Override
    public void save(ArticleReading reading) {
        store.put(reading.id().value(), reading);
    }

    @Override
    public Optional<ArticleReading> findById(ArticleReadingId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<ArticleReading> findByUserIdOrderByCreatedAtDesc(UUID userId) {
        return store.values().stream()
                .filter(r -> r.userId().equals(userId))
                .sorted(Comparator.comparing(ArticleReading::createdAt).reversed())
                .toList();
    }

    public int count() { return store.size(); }
    public void clear() { store.clear(); }
}
