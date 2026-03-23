package com.faus535.englishtrainer.auth;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.auth.infrastructure.google.GoogleTokenVerifier;
import com.faus535.englishtrainer.auth.infrastructure.google.GoogleUserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GoogleAuthIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    @MockitoBean
    private GoogleTokenVerifier googleTokenVerifier;

    @Test
    void google_login_creates_new_user_and_returns_auth_response() throws GoogleAuthException {
        when(googleTokenVerifier.verify("valid-google-token"))
                .thenReturn(new GoogleUserInfo("googleuser@gmail.com", "Google User", true));

        var body = Map.of("idToken", "valid-google-token");
        var response = rest.postForEntity("/api/auth/google", body, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKeys("token", "refreshToken", "profileId", "email");
        assertThat(response.getBody().get("email")).isEqualTo("googleuser@gmail.com");
    }

    @Test
    void google_login_returns_existing_user_when_email_already_registered() throws GoogleAuthException {
        when(googleTokenVerifier.verify("token-existing"))
                .thenReturn(new GoogleUserInfo("existing-google@gmail.com", "Existing", true));

        var body = Map.of("idToken", "token-existing");

        var first = rest.postForEntity("/api/auth/google", body, Map.class);
        var second = rest.postForEntity("/api/auth/google", body, Map.class);

        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(first.getBody().get("profileId")).isEqualTo(second.getBody().get("profileId"));
    }

    @Test
    void google_login_returns_401_when_token_is_invalid() throws GoogleAuthException {
        when(googleTokenVerifier.verify("invalid-token"))
                .thenThrow(new GoogleAuthException("Invalid Google ID token"));

        var body = Map.of("idToken", "invalid-token");
        var response = rest.postForEntity("/api/auth/google", body, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void google_login_returns_400_when_body_is_empty() {
        var response = rest.postForEntity("/api/auth/google", Map.of(), Map.class);

        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void google_login_returns_401_when_email_not_verified() throws GoogleAuthException {
        when(googleTokenVerifier.verify("unverified-token"))
                .thenReturn(new GoogleUserInfo("unverified@gmail.com", "User", false));

        var body = Map.of("idToken", "unverified-token");
        var response = rest.postForEntity("/api/auth/google", body, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void local_user_can_login_with_google_same_email() throws GoogleAuthException {
        var registerBody = Map.of(
                "email", "both@example.com",
                "password", "password123"
        );
        rest.postForEntity("/api/auth/register", registerBody, Map.class);

        when(googleTokenVerifier.verify("token-local-user"))
                .thenReturn(new GoogleUserInfo("both@example.com", "Both User", true));

        var googleBody = Map.of("idToken", "token-local-user");
        var response = rest.postForEntity("/api/auth/google", googleBody, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("email")).isEqualTo("both@example.com");
    }
}
