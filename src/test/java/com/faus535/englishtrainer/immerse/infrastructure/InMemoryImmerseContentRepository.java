package com.faus535.englishtrainer.immerse.infrastructure;

import com.faus535.englishtrainer.immerse.domain.*;

import java.util.*;

public class InMemoryImmerseContentRepository implements ImmerseContentRepository {

    private final Map<UUID, ImmerseContent> store = new LinkedHashMap<>();

    @Override
    public ImmerseContent save(ImmerseContent content) {
        store.put(content.id().value(), content);
        return content;
    }

    @Override
    public Optional<ImmerseContent> findById(ImmerseContentId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<ImmerseContent> findByUserId(UUID userId, int page, int size) {
        return store.values().stream()
                .filter(c -> c.userId().equals(userId))
                .sorted(Comparator.comparing(ImmerseContent::createdAt).reversed())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public Optional<ImmerseContent> findLatestByUserId(UUID userId) {
        return store.values().stream()
                .filter(c -> c.userId().equals(userId))
                .max(Comparator.comparing(ImmerseContent::createdAt));
    }
}
