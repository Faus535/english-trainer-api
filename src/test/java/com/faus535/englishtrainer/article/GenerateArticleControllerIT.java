package com.faus535.englishtrainer.article;

import com.faus535.englishtrainer.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateArticleControllerIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    private String validToken;

    @BeforeEach
    void setUp() {
        var body = Map.of(
                "email", "article-gen-test-" + UUID.randomUUID() + "@example.com",
                "password", "password123"
        );
        var response = rest.postForEntity("/api/auth/register", body, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        validToken = (String) response.getBody().get("token");
    }

    @Test
    void shouldReturn202WithPendingArticleWhenValidRequest() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        var requestBody = Map.of("topic", "Artificial Intelligence", "level", "B2");

        var response = rest.exchange(
                "/api/article/generate",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).containsKey("id");
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("PENDING");
        assertThat(response.getBody()).containsKey("createdAt");
        assertThat(response.getBody().get("createdAt")).isNotNull();
    }

    @Test
    void shouldReturn401WhenNoJwtProvided() {
        var requestBody = Map.of("topic", "Artificial Intelligence", "level", "B2");

        var response = rest.postForEntity("/api/article/generate", requestBody, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldReturn400WhenTopicIsBlank() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        var requestBody = Map.of("topic", "", "level", "B2");

        var response = rest.exchange(
                "/api/article/generate",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn400WhenLevelIsInvalid() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        var requestBody = Map.of("topic", "Technology", "level", "A1");

        var response = rest.exchange(
                "/api/article/generate",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
