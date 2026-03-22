package com.faus535.englishtrainer.auth.domain.error;

public final class AccountUsesGoogleException extends AuthException {

    public AccountUsesGoogleException() {
        super("This account uses Google Sign-In");
    }
}
