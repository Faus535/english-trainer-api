package com.faus535.englishtrainer.activity.infrastructure;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateMother;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityDateRepositoryIT extends IntegrationTestBase {

    @Autowired
    private ActivityDateRepository activityDateRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void save_and_find_by_user_and_date_round_trip() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        LocalDate today = LocalDate.now();
        ActivityDate activityDate = ActivityDateMother.create(userProfile.id(), today);

        activityDateRepository.save(activityDate);

        Optional<ActivityDate> found = activityDateRepository.findByUserAndDate(userProfile.id(), today);

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(activityDate.id());
        assertThat(found.get().userId()).isEqualTo(activityDate.userId());
        assertThat(found.get().activityDate()).isEqualTo(activityDate.activityDate());
    }

    @Test
    void find_all_by_user_returns_all_activity_dates() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        ActivityDate date1 = ActivityDateMother.create(userProfile.id(), LocalDate.of(2026, 3, 1));
        ActivityDate date2 = ActivityDateMother.create(userProfile.id(), LocalDate.of(2026, 3, 2));
        ActivityDate date3 = ActivityDateMother.create(userProfile.id(), LocalDate.of(2026, 3, 3));
        activityDateRepository.save(date1);
        activityDateRepository.save(date2);
        activityDateRepository.save(date3);

        List<ActivityDate> found = activityDateRepository.findAllByUser(userProfile.id());

        assertThat(found).hasSize(3);
    }

    @Test
    void find_by_user_and_month_filters_correctly() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        ActivityDate marchDate = ActivityDateMother.create(userProfile.id(), LocalDate.of(2026, 3, 15));
        ActivityDate aprilDate = ActivityDateMother.create(userProfile.id(), LocalDate.of(2026, 4, 10));
        activityDateRepository.save(marchDate);
        activityDateRepository.save(aprilDate);

        List<ActivityDate> marchDates = activityDateRepository.findByUserAndMonth(userProfile.id(), 2026, 3);

        assertThat(marchDates).hasSize(1);
        assertThat(marchDates.getFirst().activityDate().getMonth().getValue()).isEqualTo(3);
    }

    @Test
    void find_by_user_and_date_returns_empty_when_not_found() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        Optional<ActivityDate> found = activityDateRepository.findByUserAndDate(
                userProfile.id(), LocalDate.of(2099, 1, 1)
        );

        assertThat(found).isEmpty();
    }
}
