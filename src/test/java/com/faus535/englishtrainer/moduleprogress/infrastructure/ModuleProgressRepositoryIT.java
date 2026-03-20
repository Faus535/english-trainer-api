package com.faus535.englishtrainer.moduleprogress.infrastructure;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressMother;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ModuleProgressRepositoryIT extends IntegrationTestBase {

    @Autowired
    private ModuleProgressRepository moduleProgressRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void save_and_find_by_user_module_and_level_round_trip() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        ModuleProgress progress = ModuleProgressMother.withUserId(userProfile.id());

        moduleProgressRepository.save(progress);

        Optional<ModuleProgress> found = moduleProgressRepository.findByUserAndModuleAndLevel(
                userProfile.id(), progress.moduleName(), progress.level()
        );

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(progress.id());
        assertThat(found.get().userId()).isEqualTo(progress.userId());
        assertThat(found.get().moduleName()).isEqualTo(progress.moduleName());
        assertThat(found.get().level()).isEqualTo(progress.level());
        assertThat(found.get().currentUnit()).isEqualTo(progress.currentUnit());
        assertThat(found.get().completedUnits()).isEqualTo(progress.completedUnits());
        assertThat(found.get().scores()).isEqualTo(progress.scores());
    }

    @Test
    void find_all_by_user_returns_all_progress_entries() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        ModuleProgress progress1 = ModuleProgress.create(
                userProfile.id(), new ModuleName("vocabulary"), new ModuleLevel("a1")
        );
        ModuleProgress progress2 = ModuleProgress.create(
                userProfile.id(), new ModuleName("grammar"), new ModuleLevel("b1")
        );
        moduleProgressRepository.save(progress1);
        moduleProgressRepository.save(progress2);

        List<ModuleProgress> found = moduleProgressRepository.findAllByUser(userProfile.id());

        assertThat(found).hasSize(2);
    }

    @Test
    void save_updates_existing_progress_with_completed_units_and_scores() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        ModuleProgress progress = ModuleProgressMother.withUserId(userProfile.id());
        moduleProgressRepository.save(progress);

        ModuleProgress updated = progress.completeUnit(0, 85);
        moduleProgressRepository.save(updated);

        Optional<ModuleProgress> found = moduleProgressRepository.findByUserAndModuleAndLevel(
                userProfile.id(), progress.moduleName(), progress.level()
        );

        assertThat(found).isPresent();
        assertThat(found.get().currentUnit()).isEqualTo(1);
        assertThat(found.get().completedUnits()).containsExactly(0);
        assertThat(found.get().scores()).containsEntry(0, 85);
    }

    @Test
    void find_by_user_module_and_level_returns_empty_when_not_found() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        Optional<ModuleProgress> found = moduleProgressRepository.findByUserAndModuleAndLevel(
                userProfile.id(), new ModuleName("pronunciation"), new ModuleLevel("c2")
        );

        assertThat(found).isEmpty();
    }
}
