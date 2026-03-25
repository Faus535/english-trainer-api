package com.faus535.englishtrainer.user.domain;

import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;

import java.util.Optional;

public interface UserProfileRepository {

    Optional<UserProfile> findById(UserProfileId id);

    Optional<UserProfile> findByIdForUpdate(UserProfileId id);

    UserProfile save(UserProfile profile);

    void deleteById(UserProfileId id);
}
