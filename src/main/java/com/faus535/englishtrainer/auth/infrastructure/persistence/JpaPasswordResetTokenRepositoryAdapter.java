package com.faus535.englishtrainer.auth.infrastructure.persistence;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.PasswordResetToken;
import com.faus535.englishtrainer.auth.domain.PasswordResetTokenRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
class JpaPasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final JpaPasswordResetTokenRepository jpaRepository;

    JpaPasswordResetTokenRepositoryAdapter(JpaPasswordResetTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.fromDomain(token);
        if (jpaRepository.existsById(token.id())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(PasswordResetTokenEntity::toDomain);
    }

    @Override
    public int countRecentByUserId(AuthUserId userId, int minutes) {
        Instant since = Instant.now().minusSeconds(minutes * 60L);
        return jpaRepository.countRecentByUserId(userId.value(), since);
    }
}
