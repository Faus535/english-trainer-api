package com.faus535.englishtrainer.gamification.infrastructure.event;

import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.faus535.englishtrainer.user.application.AddXpUseCase;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleCompletedGamificationListenerTest {

    @Mock
    private AddXpUseCase addXpUseCase;

    private InMemoryAuthUserRepository authUserRepository;
    private ArticleCompletedGamificationListener listener;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        listener = new ArticleCompletedGamificationListener(authUserRepository, addXpUseCase);
    }

    @Test
    void xpIsAwardedOnValidEvent() throws Exception {
        AuthUser authUser = AuthUserMother.create();
        authUserRepository.save(authUser);
        UUID userId = authUser.id().value();

        listener.handle(new ArticleReadingCompletedEvent(UUID.randomUUID(), userId, 41));

        verify(addXpUseCase).execute(eq(authUser.userProfileId()), eq(41));
    }

    @Test
    void authUserNotFoundIsHandledGracefully() {
        UUID unknownUserId = UUID.randomUUID();

        assertDoesNotThrow(() ->
                listener.handle(new ArticleReadingCompletedEvent(UUID.randomUUID(), unknownUserId, 25)));
        verifyNoInteractions(addXpUseCase);
    }

    private static void assertDoesNotThrow(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new AssertionError("Expected no exception but got: " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }
}
