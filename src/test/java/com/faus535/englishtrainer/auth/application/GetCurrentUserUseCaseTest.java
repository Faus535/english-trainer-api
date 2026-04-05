package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetCurrentUserUseCaseTest {

    private InMemoryAuthUserRepository authUserRepository;
    private GetCurrentUserUseCase useCase;

    @BeforeEach
    void setUp() {
        authUserRepository = new InMemoryAuthUserRepository();
        useCase = new GetCurrentUserUseCase(authUserRepository);
    }

    @Test
    void shouldReturnUserWhenFound() throws Exception {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        AuthUser result = useCase.execute(user.id());

        assertEquals(user.id(), result.id());
        assertEquals(user.email(), result.email());
    }

    @Test
    void shouldThrowNotFoundForMissingUser() {
        AuthUserId missingId = AuthUserId.generate();
        assertThrows(NotFoundException.class,
                () -> useCase.execute(missingId));
    }
}
