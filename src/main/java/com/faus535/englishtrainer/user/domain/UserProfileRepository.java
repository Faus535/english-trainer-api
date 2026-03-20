package com.faus535.englishtrainer.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository {
    Optional<UserProfile> findById(UUID id);
    UserProfile save(UserProfile profile);
}
