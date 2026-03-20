package com.faus535.englishtrainer.user.infrastructure;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileRepositoryIT extends IntegrationTestBase {

    @Autowired
    private UserProfileRepository repository;

    @Test
    void save_and_find_by_id_round_trip() {
        UserProfile profile = UserProfileMother.create();

        repository.save(profile);

        Optional<UserProfile> found = repository.findById(profile.id());

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(profile.id());
        assertThat(found.get().testCompleted()).isEqualTo(profile.testCompleted());
        assertThat(found.get().levelListening()).isEqualTo(profile.levelListening());
        assertThat(found.get().levelVocabulary()).isEqualTo(profile.levelVocabulary());
        assertThat(found.get().levelGrammar()).isEqualTo(profile.levelGrammar());
        assertThat(found.get().levelPhrases()).isEqualTo(profile.levelPhrases());
        assertThat(found.get().levelPronunciation()).isEqualTo(profile.levelPronunciation());
        assertThat(found.get().sessionCount()).isEqualTo(profile.sessionCount());
        assertThat(found.get().sessionsThisWeek()).isEqualTo(profile.sessionsThisWeek());
        assertThat(found.get().weekStart()).isEqualTo(profile.weekStart());
        assertThat(found.get().xp()).isEqualTo(profile.xp());
    }

    @Test
    void save_with_test_completed_maps_correctly() {
        UserProfile profile = UserProfileMother.withTestCompleted();

        repository.save(profile);

        Optional<UserProfile> found = repository.findById(profile.id());

        assertThat(found).isPresent();
        assertThat(found.get().testCompleted()).isTrue();
    }

    @Test
    void find_by_id_returns_empty_when_not_found() {
        Optional<UserProfile> found = repository.findById(
                com.faus535.englishtrainer.user.domain.UserProfileId.generate()
        );

        assertThat(found).isEmpty();
    }

    @Test
    void save_updates_existing_profile() throws Exception {
        UserProfile profile = UserProfileMother.create();
        repository.save(profile);

        UserProfile updated = profile.addXp(50);
        repository.save(updated);

        Optional<UserProfile> found = repository.findById(profile.id());

        assertThat(found).isPresent();
        assertThat(found.get().xp()).isEqualTo(50);
    }

    @Test
    void delete_by_id_removes_profile() {
        UserProfile profile = UserProfileMother.create();
        repository.save(profile);

        repository.deleteById(profile.id());

        Optional<UserProfile> found = repository.findById(profile.id());
        assertThat(found).isEmpty();
    }
}
