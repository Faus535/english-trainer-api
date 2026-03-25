package com.faus535.englishtrainer.user.infrastructure.persistence;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaUserProfileRepositoryAdapter implements UserProfileRepository {

    private final JpaUserProfileRepository jpaRepository;

    JpaUserProfileRepositoryAdapter(JpaUserProfileRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserProfile> findById(UserProfileId id) {
        return jpaRepository.findById(id.value())
                .map(UserProfileEntity::toAggregate);
    }

    @Override
    public Optional<UserProfile> findByIdForUpdate(UserProfileId id) {
        return jpaRepository.findByIdForUpdate(id.value())
                .map(UserProfileEntity::toAggregate);
    }

    @Override
    public void deleteById(UserProfileId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public UserProfile save(UserProfile profile) {
        UserProfileEntity entity = UserProfileEntity.fromAggregate(profile);
        return jpaRepository.save(entity).toAggregate();
    }
}
