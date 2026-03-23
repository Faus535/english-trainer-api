package com.faus535.englishtrainer.auth.infrastructure.google;

import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleTokenVerifierTest {

    private GoogleIdTokenVerifier googleIdTokenVerifier;
    private GoogleTokenVerifier tokenVerifier;

    @BeforeEach
    void setUp() {
        googleIdTokenVerifier = mock(GoogleIdTokenVerifier.class);
        tokenVerifier = new GoogleTokenVerifier(googleIdTokenVerifier);
    }

    @Test
    void shouldReturnGoogleUserInfoWhenTokenIsValid() throws Exception {
        GoogleIdToken idToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("user@gmail.com");
        payload.setEmailVerified(true);
        payload.set("name", "Test User");

        when(googleIdTokenVerifier.verify("valid-token")).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);

        GoogleUserInfo result = tokenVerifier.verify("valid-token");

        assertEquals("user@gmail.com", result.email());
        assertEquals("Test User", result.name());
        assertTrue(result.emailVerified());
    }

    @Test
    void shouldThrowWhenTokenIsNull() throws Exception {
        when(googleIdTokenVerifier.verify("invalid-token")).thenReturn(null);

        GoogleAuthException ex = assertThrows(
                GoogleAuthException.class, () -> tokenVerifier.verify("invalid-token"));
        assertEquals("Invalid Google ID token", ex.getMessage());
    }

    @Test
    void shouldThrowWhenVerifierThrowsException() throws Exception {
        when(googleIdTokenVerifier.verify("bad-token"))
                .thenThrow(new GeneralSecurityException("signature mismatch"));

        GoogleAuthException ex = assertThrows(
                GoogleAuthException.class, () -> tokenVerifier.verify("bad-token"));
        assertTrue(ex.getMessage().contains("Failed to verify Google token"));
    }

    @Test
    void shouldThrowWhenVerifierThrowsIOException() throws Exception {
        when(googleIdTokenVerifier.verify("network-error"))
                .thenThrow(new IOException("network error"));

        GoogleAuthException ex = assertThrows(
                GoogleAuthException.class, () -> tokenVerifier.verify("network-error"));
        assertTrue(ex.getMessage().contains("Failed to verify Google token"));
    }

    @Test
    void shouldReturnUnverifiedEmailWhenGoogleSaysNotVerified() throws Exception {
        GoogleIdToken idToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail("unverified@gmail.com");
        payload.setEmailVerified(false);
        payload.set("name", "Unverified");

        when(googleIdTokenVerifier.verify("unverified-token")).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);

        GoogleUserInfo result = tokenVerifier.verify("unverified-token");

        assertFalse(result.emailVerified());
    }
}
