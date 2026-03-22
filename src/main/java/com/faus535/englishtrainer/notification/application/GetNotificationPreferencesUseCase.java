package com.faus535.englishtrainer.notification.application;

import com.faus535.englishtrainer.notification.domain.NotificationPreferences;
import com.faus535.englishtrainer.notification.domain.NotificationPreferencesRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class GetNotificationPreferencesUseCase {

    private final NotificationPreferencesRepository repository;

    public GetNotificationPreferencesUseCase(NotificationPreferencesRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public NotificationPreferences execute(UUID userId) {
        return repository.findByUserId(userId)
                .orElse(NotificationPreferences.defaults(userId));
    }
}
