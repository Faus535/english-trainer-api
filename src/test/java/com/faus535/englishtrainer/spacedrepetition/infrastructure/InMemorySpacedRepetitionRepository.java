package com.faus535.englishtrainer.spacedrepetition.infrastructure;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.*;

public final class InMemorySpacedRepetitionRepository implements SpacedRepetitionRepository {

    private final Map<UUID, SpacedRepetitionItem> store = new HashMap<>();

    @Override
    public Optional<SpacedRepetitionItem> findById(SpacedRepetitionItemId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public Optional<SpacedRepetitionItem> findByUserAndUnitReference(UserProfileId userId, String unitReference) {
        return store.values().stream()
                .filter(i -> i.userId().equals(userId) && i.unitReference().equals(unitReference))
                .findFirst();
    }

    @Override
    public List<SpacedRepetitionItem> findDueByUser(UserProfileId userId, LocalDate today) {
        return store.values().stream()
                .filter(i -> i.userId().equals(userId)
                        && !i.graduated()
                        && !i.nextReviewDate().isAfter(today))
                .toList();
    }

    @Override
    public List<SpacedRepetitionItem> findAllByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(i -> i.userId().equals(userId))
                .toList();
    }

    @Override
    public SpacedRepetitionItem save(SpacedRepetitionItem item) {
        store.put(item.id().value(), item);
        return item;
    }
}
