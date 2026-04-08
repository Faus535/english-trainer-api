package com.faus535.englishtrainer.activity.infrastructure.event;

import com.faus535.englishtrainer.activity.application.RecordActivityUseCase;
import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleCompletedActivityListenerTest {

    @Mock
    private RecordActivityUseCase recordActivityUseCase;

    private InMemoryAuthUserRepository authUserRepository;
    private ArticleCompletedActivityListener listener;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        listener = new ArticleCompletedActivityListener(authUserRepository, recordActivityUseCase);
    }

    @Test
    void activityIsRecordedOnValidEvent() {
        AuthUser authUser = AuthUserMother.create();
        authUserRepository.save(authUser);
        UUID userId = authUser.id().value();

        listener.handle(new ArticleReadingCompletedEvent(UUID.randomUUID(), userId, 25));

        verify(recordActivityUseCase).execute(eq(authUser.userProfileId()), any(LocalDate.class));
    }

    @Test
    void authUserNotFoundIsHandledGracefully() {
        UUID unknownUserId = UUID.randomUUID();

        assertDoesNotThrow(() ->
                listener.handle(new ArticleReadingCompletedEvent(UUID.randomUUID(), unknownUserId, 25)));
        verifyNoInteractions(recordActivityUseCase);
    }

    private static void assertDoesNotThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new AssertionError("Expected no exception but got: " + e.getMessage(), e);
        }
    }
}
