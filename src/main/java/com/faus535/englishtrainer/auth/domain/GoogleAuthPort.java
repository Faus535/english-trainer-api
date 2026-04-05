package com.faus535.englishtrainer.auth.domain;

import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;

public interface GoogleAuthPort {

    GoogleVerifiedUser verify(String idToken) throws GoogleAuthException;
}
