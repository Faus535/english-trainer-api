package com.faus535.englishtrainer.auth;

import com.faus535.englishtrainer.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void register_and_login_flow() {
        // Register
        var registerBody = Map.of(
                "email", "test@example.com",
                "password", "password123"
        );
        var registerResponse = rest.postForEntity("/api/auth/register", registerBody, Map.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).containsKeys("token", "refreshToken", "profileId");

        String token = (String) registerResponse.getBody().get("token");
        String profileId = (String) registerResponse.getBody().get("profileId");

        // Login
        var loginBody = Map.of(
                "email", "test@example.com",
                "password", "password123"
        );
        var loginResponse = rest.postForEntity("/api/auth/login", loginBody, Map.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).containsKeys("token", "profileId");

        // Get profile with token
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var profileResponse = rest.exchange(
                "/api/profiles/" + profileId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profileResponse.getBody()).containsEntry("testCompleted", false);
    }

    @Test
    void register_duplicate_email_returns_409() {
        var body = Map.of(
                "email", "duplicate@example.com",
                "password", "password123"
        );
        rest.postForEntity("/api/auth/register", body, Map.class);

        var duplicateResponse = rest.postForEntity("/api/auth/register", body, Map.class);
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_wrong_password_returns_401() {
        var registerBody = Map.of(
                "email", "wrong@example.com",
                "password", "password123"
        );
        rest.postForEntity("/api/auth/register", registerBody, Map.class);

        var loginBody = Map.of(
                "email", "wrong@example.com",
                "password", "wrongpassword"
        );
        var response = rest.postForEntity("/api/auth/login", loginBody, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
