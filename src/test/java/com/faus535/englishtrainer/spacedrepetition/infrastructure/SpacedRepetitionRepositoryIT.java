package com.faus535.englishtrainer.spacedrepetition.infrastructure;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemMother;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SpacedRepetitionRepositoryIT extends IntegrationTestBase {

    @Autowired
    private SpacedRepetitionRepository spacedRepetitionRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void save_and_find_by_id_round_trip() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        SpacedRepetitionItem item = SpacedRepetitionItemMother.create(userProfile.id());

        spacedRepetitionRepository.save(item);

        Optional<SpacedRepetitionItem> found = spacedRepetitionRepository.findById(item.id());

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(item.id());
        assertThat(found.get().userId()).isEqualTo(item.userId());
        assertThat(found.get().unitReference()).isEqualTo(item.unitReference());
        assertThat(found.get().moduleName()).isEqualTo(item.moduleName());
        assertThat(found.get().level()).isEqualTo(item.level());
        assertThat(found.get().unitIndex()).isEqualTo(item.unitIndex());
        assertThat(found.get().nextReviewDate()).isEqualTo(item.nextReviewDate());
        assertThat(found.get().intervalIndex()).isEqualTo(item.intervalIndex());
        assertThat(found.get().reviewCount()).isEqualTo(item.reviewCount());
        assertThat(found.get().graduated()).isEqualTo(item.graduated());
    }

    @Test
    void find_by_user_and_unit_reference() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        SpacedRepetitionItem item = SpacedRepetitionItemMother.create(userProfile.id(), "grammar", "B1", 2);
        spacedRepetitionRepository.save(item);

        Optional<SpacedRepetitionItem> found = spacedRepetitionRepository.findByUserAndUnitReference(
                userProfile.id(), "grammar-B1-2"
        );

        assertThat(found).isPresent();
        assertThat(found.get().moduleName()).isEqualTo("grammar");
    }

    @Test
    void find_due_by_user_returns_items_due_today_or_earlier() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        SpacedRepetitionItem item = SpacedRepetitionItemMother.create(userProfile.id(), "vocabulary", "A1", 0);
        spacedRepetitionRepository.save(item);

        // The item's next_review_date is tomorrow by default, so querying for tomorrow should find it
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<SpacedRepetitionItem> due = spacedRepetitionRepository.findDueByUser(userProfile.id(), tomorrow);

        assertThat(due).hasSize(1);
    }

    @Test
    void find_all_by_user_returns_all_items() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        SpacedRepetitionItem item1 = SpacedRepetitionItemMother.create(userProfile.id(), "vocabulary", "A1", 0);
        SpacedRepetitionItem item2 = SpacedRepetitionItemMother.create(userProfile.id(), "grammar", "A1", 0);
        spacedRepetitionRepository.save(item1);
        spacedRepetitionRepository.save(item2);

        List<SpacedRepetitionItem> found = spacedRepetitionRepository.findAllByUser(userProfile.id());

        assertThat(found).hasSize(2);
    }

    @Test
    void save_updates_existing_item_after_review() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        SpacedRepetitionItem item = SpacedRepetitionItemMother.create(userProfile.id());
        spacedRepetitionRepository.save(item);

        SpacedRepetitionItem reviewed = item.completeReview();
        spacedRepetitionRepository.save(reviewed);

        Optional<SpacedRepetitionItem> found = spacedRepetitionRepository.findById(item.id());

        assertThat(found).isPresent();
        assertThat(found.get().reviewCount()).isEqualTo(1);
        assertThat(found.get().intervalIndex()).isEqualTo(1);
    }
}
