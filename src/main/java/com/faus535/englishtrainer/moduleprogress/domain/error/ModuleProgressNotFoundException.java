package com.faus535.englishtrainer.moduleprogress.domain.error;

import com.faus535.englishtrainer.shared.domain.error.NotFoundException;

public final class ModuleProgressNotFoundException extends NotFoundException {

    public ModuleProgressNotFoundException(String module, String level) {
        super("Module progress not found for module: " + module + ", level: " + level);
    }
}
