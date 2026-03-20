package com.faus535.englishtrainer.activity.infrastructure.persistence;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
class JpaActivityDateRepositoryAdapter implements ActivityDateRepository {

    private final JpaActivityDateRepository jpaRepository;

    JpaActivityDateRepositoryAdapter(JpaActivityDateRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ActivityDate> findByUserAndDate(UserProfileId userId, LocalDate date) {
        return jpaRepository.findByUserIdAndActivityDate(userId.value(), date)
                .map(ActivityDateEntity::toAggregate);
    }

    @Override
    public List<ActivityDate> findAllByUser(UserProfileId userId) {
        return jpaRepository.findByUserIdOrderByActivityDateDesc(userId.value()).stream()
                .map(ActivityDateEntity::toAggregate)
                .toList();
    }

    @Override
    public List<ActivityDate> findByUserAndMonth(UserProfileId userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return jpaRepository.findByUserIdAndActivityDateBetween(userId.value(), start, end).stream()
                .map(ActivityDateEntity::toAggregate)
                .toList();
    }

    @Override
    public ActivityDate save(ActivityDate activityDate) {
        ActivityDateEntity entity = ActivityDateEntity.fromAggregate(activityDate);
        if (jpaRepository.existsById(activityDate.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}
