package com.faus535.englishtrainer.user.infrastructure;

import com.faus535.englishtrainer.user.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserProfileRepository extends JpaRepository<UserProfile, UUID> {
}
