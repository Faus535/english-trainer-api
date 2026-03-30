package com.faus535.englishtrainer.shared.infrastructure.error;

import com.faus535.englishtrainer.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidUuidPathVariableIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    private String validToken;
    private String profileId;

    @BeforeEach
    void setUp() {
        var body = Map.of(
                "email", "uuid-test-" + UUID.randomUUID() + "@example.com",
                "password", "password123"
        );
        var response = rest.postForEntity("/api/auth/register", body, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        validToken = (String) response.getBody().get("token");
        profileId = (String) response.getBody().get("profileId");
    }

    @Test
    void invalid_uuid_path_variable_returns_400() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var response = rest.exchange(
                "/api/profiles/" + profileId + "/sessions/not-a-uuid/blocks/0/advance",
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("code", "invalid_parameter");
        assertThat((String) response.getBody().get("message")).contains("sessionId");
        assertThat((String) response.getBody().get("message")).doesNotContain("not-a-uuid");
    }
}
