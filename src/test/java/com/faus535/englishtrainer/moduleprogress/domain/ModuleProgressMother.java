package com.faus535.englishtrainer.moduleprogress.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class ModuleProgressMother {

    private ModuleProgressMother() {
    }

    public static ModuleProgress create() {
        return ModuleProgress.create(
                UserProfileId.generate(),
                new ModuleName("vocabulary"),
                new ModuleLevel("b1")
        );
    }

    public static ModuleProgress withModule(String moduleName) {
        return ModuleProgress.create(
                UserProfileId.generate(),
                new ModuleName(moduleName),
                new ModuleLevel("a1")
        );
    }

    public static ModuleProgress withUserId(UserProfileId userId) {
        return ModuleProgress.create(
                userId,
                new ModuleName("grammar"),
                new ModuleLevel("a2")
        );
    }
}
