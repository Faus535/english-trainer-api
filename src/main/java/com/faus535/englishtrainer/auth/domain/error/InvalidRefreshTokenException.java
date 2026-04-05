package com.faus535.englishtrainer.auth.domain.error;

public final class InvalidRefreshTokenException extends AuthException {

    public InvalidRefreshTokenException() {
        super("Invalid or expired refresh token");
    }
}
