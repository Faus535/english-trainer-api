package com.faus535.englishtrainer.user.domain.error;

public final class InvalidModuleException extends UserProfileException {

    public InvalidModuleException(String module) {
        super("Unknown module: " + module);
    }
}
