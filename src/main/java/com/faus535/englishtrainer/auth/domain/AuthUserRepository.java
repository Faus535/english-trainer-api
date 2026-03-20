package com.faus535.englishtrainer.auth.domain;

import java.util.Optional;

public interface AuthUserRepository {

    Optional<AuthUser> findByEmail(String email);

    Optional<AuthUser> findById(AuthUserId id);

    boolean existsByEmail(String email);

    AuthUser save(AuthUser user);
}
