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
        assertThat(found.get().xp()).isEqualTo(profile.xp());
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
