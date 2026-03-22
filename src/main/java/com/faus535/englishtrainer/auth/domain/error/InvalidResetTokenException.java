package com.faus535.englishtrainer.auth.domain.error;

public final class InvalidResetTokenException extends AuthException {

    public InvalidResetTokenException() {
        super("Invalid or expired reset token");
    }
}
