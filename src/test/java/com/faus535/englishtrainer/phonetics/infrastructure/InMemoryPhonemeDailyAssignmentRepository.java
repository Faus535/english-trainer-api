package com.faus535.englishtrainer.phonetics.infrastructure;

import com.faus535.englishtrainer.phonetics.domain.PhonemeDailyAssignment;
import com.faus535.englishtrainer.phonetics.domain.PhonemeDailyAssignmentRepository;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class InMemoryPhonemeDailyAssignmentRepository implements PhonemeDailyAssignmentRepository {

    private final Map<UUID, PhonemeDailyAssignment> store = new HashMap<>();

    @Override
    public Optional<PhonemeDailyAssignment> findByUserAndDate(UserProfileId userId, LocalDate date) {
        return store.values().stream()
                .filter(a -> a.userId().equals(userId) && a.assignedDate().equals(date))
                .findFirst();
    }

    @Override
    public Optional<PhonemeDailyAssignment> findByUserAndPhoneme(UserProfileId userId, PhonemeId phonemeId) {
        return store.values().stream()
                .filter(a -> a.userId().equals(userId) && a.phonemeId().equals(phonemeId))
                .findFirst();
    }

    @Override
    public List<PhonemeDailyAssignment> findCompletedByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(a -> a.userId().equals(userId) && a.completed())
                .toList();
    }

    @Override
    public PhonemeDailyAssignment save(PhonemeDailyAssignment assignment) {
        store.put(assignment.id().value(), assignment);
        return assignment;
    }
}
