package com.faus535.englishtrainer.auth.infrastructure.persistence;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.RefreshTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRepository;

    JpaRefreshTokenRepositoryAdapter(JpaRefreshTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = RefreshTokenEntity.fromDomain(token);
        if (jpaRepository.existsById(token.id())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(RefreshTokenEntity::toDomain);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(AuthUserId userId) {
        jpaRepository.revokeAllByUserId(userId.value());
    }
}
