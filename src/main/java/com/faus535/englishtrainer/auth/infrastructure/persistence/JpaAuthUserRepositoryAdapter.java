package com.faus535.englishtrainer.auth.infrastructure.persistence;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaAuthUserRepositoryAdapter implements AuthUserRepository {

    private final JpaAuthUserRepository jpaRepository;

    JpaAuthUserRepositoryAdapter(JpaAuthUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<AuthUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(AuthUserEntity::toAggregate);
    }

    @Override
    public Optional<AuthUser> findById(AuthUserId id) {
        return jpaRepository.findById(id.value())
                .map(AuthUserEntity::toAggregate);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public AuthUser save(AuthUser user) {
        AuthUserEntity entity = AuthUserEntity.fromAggregate(user);
        if (jpaRepository.existsById(user.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }

    @Override
    public void deleteById(AuthUserId id) {
        jpaRepository.deleteById(id.value());
    }
}
