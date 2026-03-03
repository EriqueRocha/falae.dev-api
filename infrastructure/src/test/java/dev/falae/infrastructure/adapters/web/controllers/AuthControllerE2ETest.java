package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.LoginRequest;
import dev.falae.application.ports.dto.LoginResponse;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataLoader testDataLoader;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
        baseUrl = "http://localhost:" + port + "/auth";
    }

    @Test
    void loginAuthor_WithValidCredentials_ReturnsOkAndToken() {
        LoginRequest request = new LoginRequest("author1@test.com", "password123");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                baseUrl + "/author/login",
                HttpMethod.POST,
                entity,
                LoginResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotNull();
    }

    @Test
    void loginAuthor_WithInvalidEmail_ReturnsNotFound() {
        LoginRequest request = new LoginRequest("nonexistent@test.com", "password123");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/author/login",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void loginAuthor_WithInvalidPassword_ReturnsUnauthorized() {
        LoginRequest request = new LoginRequest("author1@test.com", "wrongpassword");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/author/login",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void loginAdmin_WithValidCredentials_ReturnsOkAndToken() {
        LoginRequest request = new LoginRequest("admin1@test.com", "admin123");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                baseUrl + "/admin/login",
                HttpMethod.POST,
                entity,
                LoginResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isNotBlank();
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotNull();
    }

    @Test
    void loginAdmin_WithInvalidEmail_ReturnsNotFound() {
        LoginRequest request = new LoginRequest("nonexistent@test.com", "admin123");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/admin/login",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void loginAdmin_WithInvalidPassword_ReturnsUnauthorized() {
        LoginRequest request = new LoginRequest("admin1@test.com", "wrongpassword");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/admin/login",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void logout_ReturnsOkAndClearsCookie() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/logout",
                HttpMethod.POST,
                HttpEntity.EMPTY,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Logout successful");
    }
}
