package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.CommentPageResponse;
import dev.falae.application.ports.dto.CommentResponse;
import dev.falae.application.ports.dto.CreateCommentRequest;
import dev.falae.application.ports.dto.CreateCommentResponse;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.CommentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommentControllerE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataLoader testDataLoader;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    private String baseUrl;
    private String authorToken;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
        baseUrl = "http://localhost:" + port + "/api/comment";
        authorToken = testDataLoader.generateAuthorToken(testDataLoader.getAuthor1());
    }

    @Test
    void saveComment_OnArticle_ReturnsCreated() {
        UUID articleId = testDataLoader.getArticle1().getId();
        CreateCommentRequest request = new CreateCommentRequest(
                "This is a new comment on article",
                articleId,
                null,
                null,
                null
        );
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<CreateCommentRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateCommentResponse> response = restTemplate.exchange(
                baseUrl + "/save",
                HttpMethod.POST,
                entity,
                CreateCommentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(commentJpaRepository.findById(response.getBody().id())).isPresent();
    }

    @Test
    void saveComment_OnTopic_ReturnsCreated() {
        UUID topicId = testDataLoader.getTopic1().getId();
        CreateCommentRequest request = new CreateCommentRequest(
                "This is a new comment on topic",
                null,
                topicId,
                null,
                null
        );
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<CreateCommentRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateCommentResponse> response = restTemplate.exchange(
                baseUrl + "/save",
                HttpMethod.POST,
                entity,
                CreateCommentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void saveComment_AsReplyToComment_ReturnsCreated() {
        UUID articleId = testDataLoader.getArticle1().getId();
        UUID parentId = testDataLoader.getArticleComment1().getId();
        CreateCommentRequest request = new CreateCommentRequest(
                "This is a reply to a comment",
                articleId,
                null,
                parentId,
                null
        );
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<CreateCommentRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateCommentResponse> response = restTemplate.exchange(
                baseUrl + "/save",
                HttpMethod.POST,
                entity,
                CreateCommentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        var savedComment = commentJpaRepository.findById(response.getBody().id());
        assertThat(savedComment).isPresent();
        assertThat(savedComment.get().getParent().getId()).isEqualTo(parentId);
    }

    @Test
    void saveComment_WithoutToken_ReturnsForbidden() {
        UUID articleId = testDataLoader.getArticle1().getId();
        CreateCommentRequest request = new CreateCommentRequest(
                "This is a comment",
                articleId,
                null,
                null,
                null
        );
        HttpEntity<CreateCommentRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/save",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void findByArticle_WithValidArticleId_ReturnsComments() {
        UUID articleId = testDataLoader.getArticle1().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CommentPageResponse> response = restTemplate.exchange(
                baseUrl + "/article/" + articleId + "?page=0&size=20",
                HttpMethod.GET,
                entity,
                CommentPageResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().comments()).isNotEmpty();
        assertThat(response.getBody().comments().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void findByArticle_WithNonExistentArticle_ReturnsEmptyList() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CommentPageResponse> response = restTemplate.exchange(
                baseUrl + "/article/" + nonExistentId,
                HttpMethod.GET,
                entity,
                CommentPageResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().comments()).isEmpty();
    }

    @Test
    void findByTopic_WithValidTopicId_ReturnsComments() {
        UUID topicId = testDataLoader.getTopic1().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CommentPageResponse> response = restTemplate.exchange(
                baseUrl + "/topic/" + topicId + "?page=0&size=20",
                HttpMethod.GET,
                entity,
                CommentPageResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().comments()).isNotEmpty();
        assertThat(response.getBody().comments().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void findByTopic_WithNonExistentTopic_ReturnsEmptyList() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CommentPageResponse> response = restTemplate.exchange(
                baseUrl + "/topic/" + nonExistentId,
                HttpMethod.GET,
                entity,
                CommentPageResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().comments()).isEmpty();
    }

    @Test
    void findReplies_WithCommentThatHasReplies_ReturnsReplies() {
        UUID commentId = testDataLoader.getArticleComment1().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<CommentResponse>> response = restTemplate.exchange(
                baseUrl + "/" + commentId + "/replies",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<CommentResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).parentId()).isEqualTo(commentId);
    }

    @Test
    void findReplies_WithCommentWithoutReplies_ReturnsEmptyList() {
        UUID commentId = testDataLoader.getArticleComment2().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<CommentResponse>> response = restTemplate.exchange(
                baseUrl + "/" + commentId + "/replies",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<CommentResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void findReplies_WithNonExistentComment_ReturnsEmptyList() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<CommentResponse>> response = restTemplate.exchange(
                baseUrl + "/" + nonExistentId + "/replies",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<CommentResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "token=" + token);
        return headers;
    }
}