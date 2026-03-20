package com.faus535.englishtrainer.user.infrastructure;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserProfileRepositoryAdapter implements UserProfileRepository {

    private final JpaUserProfileRepository jpa;

    public UserProfileRepositoryAdapter(JpaUserProfileRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<UserProfile> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public UserProfile save(UserProfile profile) {
        return jpa.save(profile);
    }
}
