package com.faus535.englishtrainer.auth;

import com.faus535.englishtrainer.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityStatusCodeTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    private String validToken;
    private String profileId;

    @BeforeEach
    void setUp() {
        var body = Map.of(
                "email", "security-test-" + UUID.randomUUID() + "@example.com",
                "password", "password123"
        );
        var response = rest.postForEntity("/api/auth/register", body, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        validToken = (String) response.getBody().get("token");
        profileId = (String) response.getBody().get("profileId");
    }

    @Test
    void request_without_token_returns_401() {
        var response = rest.getForEntity("/api/profiles/" + profileId, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void request_with_expired_token_returns_401() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIiwicHJvZmlsZUlkIjoiMTIzNDU2NzgtMTIzNC0xMjM0LTEyMzQtMTIzNDU2Nzg5MDEyIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjIzOTAyMn0." +
                "invalid-signature";

        var headers = new HttpHeaders();
        headers.setBearerAuth(expiredToken);
        var response = rest.exchange(
                "/api/profiles/" + profileId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void request_with_valid_token_returns_200() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        var response = rest.exchange(
                "/api/profiles/" + profileId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void put_levels_with_wrong_profile_returns_403() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        UUID otherProfileId = UUID.randomUUID();
        var body = Map.of("levels", Map.of("listening", "b1"));

        var response = rest.exchange(
                "/api/profiles/" + otherProfileId + "/levels",
                HttpMethod.PUT,
                new HttpEntity<>(body, headers),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void put_levels_with_own_profile_returns_204() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var body = Map.of("levels", Map.of(
                "listening", "b1",
                "vocabulary", "b1",
                "grammar", "b1",
                "pronunciation", "b1",
                "phrases", "b1"
        ));

        var response = rest.exchange(
                "/api/profiles/" + profileId + "/levels",
                HttpMethod.PUT,
                new HttpEntity<>(body, headers),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
