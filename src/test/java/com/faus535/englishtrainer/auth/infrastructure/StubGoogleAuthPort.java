package com.faus535.englishtrainer.auth.infrastructure;

import com.faus535.englishtrainer.auth.domain.GoogleAuthPort;
import com.faus535.englishtrainer.auth.domain.GoogleVerifiedUser;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;

public final class StubGoogleAuthPort implements GoogleAuthPort {

    private GoogleVerifiedUser result;
    private GoogleAuthException exception;

    public void willReturn(GoogleVerifiedUser result) {
        this.result = result;
        this.exception = null;
    }

    public void willThrow(GoogleAuthException exception) {
        this.exception = exception;
        this.result = null;
    }

    @Override
    public GoogleVerifiedUser verify(String idToken) throws GoogleAuthException {
        if (exception != null) {
            throw exception;
        }
        return result;
    }
}
