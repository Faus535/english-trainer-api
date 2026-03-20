package com.faus535.englishtrainer.spacedrepetition.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpacedRepetitionRepository {
    Optional<SpacedRepetitionItem> findById(SpacedRepetitionItemId id);
    Optional<SpacedRepetitionItem> findByUserAndUnitReference(UserProfileId userId, String unitReference);
    List<SpacedRepetitionItem> findDueByUser(UserProfileId userId, LocalDate today);
    List<SpacedRepetitionItem> findAllByUser(UserProfileId userId);
    SpacedRepetitionItem save(SpacedRepetitionItem item);
}
