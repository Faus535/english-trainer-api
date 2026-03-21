package com.faus535.englishtrainer.auth.domain.error;

public final class AccountUsesGoogleException extends Exception {

    public AccountUsesGoogleException() {
        super("This account uses Google Sign-In");
    }
}
