package com.faus535.englishtrainer.user.domain.error;

public final class InvalidModuleException extends Exception {

    public InvalidModuleException(String module) {
        super("Unknown module: " + module);
    }
}
