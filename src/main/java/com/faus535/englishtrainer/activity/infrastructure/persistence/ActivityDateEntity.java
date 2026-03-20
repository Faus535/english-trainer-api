package com.faus535.englishtrainer.activity.infrastructure.persistence;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "activity_dates")
class ActivityDateEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    protected ActivityDateEntity() {}

    static ActivityDateEntity fromAggregate(ActivityDate aggregate) {
        ActivityDateEntity entity = new ActivityDateEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.activityDate = aggregate.activityDate();
        return entity;
    }

    ActivityDate toAggregate() {
        return ActivityDate.reconstitute(
                new ActivityDateId(id),
                new UserProfileId(userId),
                activityDate
        );
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
