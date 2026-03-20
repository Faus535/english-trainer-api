package com.faus535.englishtrainer.moduleprogress.application;

import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.infrastructure.InMemoryModuleProgressRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class InitModuleProgressUseCaseTest {

    private InMemoryModuleProgressRepository repository;
    private InitModuleProgressUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryModuleProgressRepository();
        useCase = new InitModuleProgressUseCase(repository);
    }

    @Test
    void shouldInitializeModuleProgress() {
        UserProfileId userId = UserProfileId.generate();
        ModuleName moduleName = new ModuleName("vocabulary");
        ModuleLevel level = new ModuleLevel("a1");

        ModuleProgress progress = useCase.execute(userId, moduleName, level);

        assertNotNull(progress);
        assertEquals(userId, progress.userId());
        assertEquals(moduleName, progress.moduleName());
        assertEquals(level, progress.level());
        assertEquals(0, progress.currentUnit());
        assertTrue(progress.completedUnits().isEmpty());
    }
}
