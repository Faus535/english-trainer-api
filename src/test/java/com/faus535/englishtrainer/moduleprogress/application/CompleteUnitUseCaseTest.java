package com.faus535.englishtrainer.moduleprogress.application;

import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.error.ModuleProgressNotFoundException;
import com.faus535.englishtrainer.moduleprogress.infrastructure.InMemoryModuleProgressRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class CompleteUnitUseCaseTest {

    private InMemoryModuleProgressRepository repository;
    private CompleteUnitUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryModuleProgressRepository();
        useCase = new CompleteUnitUseCase(repository);
    }

    @Test
    void shouldCompleteUnitAndUpdateProgress() throws ModuleProgressNotFoundException {
        UserProfileId userId = UserProfileId.generate();
        ModuleName moduleName = new ModuleName("vocabulary");
        ModuleLevel level = new ModuleLevel("a1");
        ModuleProgress progress = ModuleProgress.create(userId, moduleName, level);
        repository.save(progress);

        ModuleProgress updated = useCase.execute(userId, moduleName, level, 0, 85);

        assertTrue(updated.completedUnits().contains(0));
        assertEquals(85, updated.scores().get(0));
        assertEquals(1, updated.currentUnit());
    }

    @Test
    void shouldThrowWhenProgressNotFound() {
        UserProfileId userId = UserProfileId.generate();
        ModuleName moduleName = new ModuleName("grammar");
        ModuleLevel level = new ModuleLevel("b1");

        assertThrows(ModuleProgressNotFoundException.class,
                () -> useCase.execute(userId, moduleName, level, 0, 90));
    }
}
