package com.faus535.englishtrainer.moduleprogress.domain.error;

public final class ModuleProgressNotFoundException extends ModuleProgressException {

    public ModuleProgressNotFoundException(String module, String level) {
        super("Module progress not found for module: " + module + ", level: " + level);
    }
}
