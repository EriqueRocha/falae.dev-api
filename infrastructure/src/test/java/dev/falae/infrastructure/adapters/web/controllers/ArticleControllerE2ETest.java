package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.CreateArticleResponse;
import dev.falae.application.ports.dto.SaveArticleRequest;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.ArticleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleControllerE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataLoader testDataLoader;

    @Autowired
    private ArticleJpaRepository articleJpaRepository;

    private String baseUrl;
    private String authorToken;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
        baseUrl = "http://localhost:" + port + "/article";
        authorToken = testDataLoader.generateAuthorToken(testDataLoader.getAuthor1());
    }

    @Test
    void saveArticle_WithValidData_ReturnsCreated() {
        SaveArticleRequest request = new SaveArticleRequest(
                "New Article Title",
                "Description of new article",
                List.of("java", "spring", "testing"),
                null
        );
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<SaveArticleRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateArticleResponse> response = restTemplate.exchange(
                baseUrl + "/saveNew",
                HttpMethod.POST,
                entity,
                CreateArticleResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().slug()).isEqualTo("new-article-title");
        assertThat(articleJpaRepository.findById(response.getBody().id())).isPresent();
    }

    @Test
    void saveArticle_WithoutToken_ReturnsForbidden() {
        SaveArticleRequest request = new SaveArticleRequest(
                "New Article Title",
                "Description of new article",
                List.of("java", "spring"),
                null
        );
        HttpEntity<SaveArticleRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/saveNew",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteArticle_WithOwnerToken_ReturnsNoContent() {
        UUID articleId = testDataLoader.getArticle1().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/delete/" + articleId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(articleJpaRepository.findById(articleId)).isEmpty();
    }

    @Test
    void deleteArticle_WithNonOwnerToken_ReturnsNotFound() {
        UUID articleId = testDataLoader.getArticle2().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/delete/" + articleId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(articleJpaRepository.findById(articleId)).isPresent();
    }

    @Test
    void deleteArticle_WithNonExistentId_ReturnsNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/delete/" + nonExistentId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteArticle_WithoutToken_ReturnsForbidden() {
        UUID articleId = testDataLoader.getArticle1().getId();
        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/delete/" + articleId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "token=" + token);
        return headers;
    }
}