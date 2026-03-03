package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.CreateAuthorRequest;
import dev.falae.application.ports.dto.CreateAuthorResponse;
import dev.falae.application.ports.dto.UpdateAuthorRequest;
import dev.falae.application.ports.dto.UpdateAuthorResponse;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.AuthorJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorControllerE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataLoader testDataLoader;

    @Autowired
    private AuthorJpaRepository authorJpaRepository;

    private String baseUrl;
    private String authorToken;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
        baseUrl = "http://localhost:" + port + "/api/authors";
        authorToken = testDataLoader.generateAuthorToken(testDataLoader.getAuthor1());
    }

    @Test
    void createAuthor_WithValidData_ReturnsCreated() {
        CreateAuthorRequest request = new CreateAuthorRequest(
                "newauthor@test.com",
                "password123",
                "New Author",
                "new-author"
        );
        HttpEntity<CreateAuthorRequest> entity = new HttpEntity<>(request);

        ResponseEntity<CreateAuthorResponse> response = restTemplate.exchange(
                baseUrl + "/create",
                HttpMethod.POST,
                entity,
                CreateAuthorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(authorJpaRepository.findByEmail("newauthor@test.com")).isPresent();
    }

    @Test
    void createAuthor_WithDuplicateEmail_ReturnsConflict() {
        CreateAuthorRequest request = new CreateAuthorRequest(
                "author1@test.com",
                "password123",
                "Duplicate Author",
                "duplicate-author"
        );
        HttpEntity<CreateAuthorRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/create",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void updateAuthor_WithValidToken_ReturnsOk() {
        UpdateAuthorRequest request = new UpdateAuthorRequest(
                "Updated Name",
                "https://github.com/updated",
                "Updated bio"
        );
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<UpdateAuthorRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<UpdateAuthorResponse> response = restTemplate.exchange(
                baseUrl + "/basic-profile-update",
                HttpMethod.PATCH,
                entity,
                UpdateAuthorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        var updatedAuthor = authorJpaRepository.findById(testDataLoader.getAuthor1().getId());
        assertThat(updatedAuthor).isPresent();
        assertThat(updatedAuthor.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedAuthor.get().getGitHub()).isEqualTo("https://github.com/updated");
        assertThat(updatedAuthor.get().getBio()).isEqualTo("Updated bio");
    }

    @Test
    void updateAuthor_WithoutToken_ReturnsUnauthorized() {
        UpdateAuthorRequest request = new UpdateAuthorRequest(
                "Updated Name",
                "https://github.com/updated",
                "Updated bio"
        );
        HttpEntity<UpdateAuthorRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/basic-profile-update",
                HttpMethod.PATCH,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "token=" + token);
        return headers;
    }
}