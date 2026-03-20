package com.faus535.englishtrainer.vocabulary;

import com.faus535.englishtrainer.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VocabIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void get_all_vocab_returns_seeded_entries() {
        var response = rest.getForEntity("/api/vocab", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1209);
    }

    @Test
    void get_vocab_by_level_filters_correctly() {
        var response = rest.getForEntity("/api/vocab/level/a1", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void get_random_vocab_returns_single_entry() {
        var response = rest.getForEntity("/api/vocab/random", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKeys("en", "es");
    }
}
