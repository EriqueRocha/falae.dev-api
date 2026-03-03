package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AdminControllerE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataLoader testDataLoader;

    @Autowired
    private ArticleJpaRepository articleJpaRepository;

    @Autowired
    private AuthorJpaRepository authorJpaRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @Autowired
    private TopicJpaRepository topicJpaRepository;

    private String adminToken;
    private String authorToken;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
        adminToken = testDataLoader.generateAdminToken(testDataLoader.getAdmin1());
        authorToken = testDataLoader.generateAuthorToken(testDataLoader.getAuthor1());
        baseUrl = "http://localhost:" + port + "/api/admin";
    }

    @Test
    void adminDeleteArticle_WithValidAdminToken_ReturnsNoContent() {
        UUID articleId = testDataLoader.getArticle1().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/article/" + articleId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(articleJpaRepository.findById(articleId)).isEmpty();
    }

    @Test
    void adminDeleteArticle_WithAuthorToken_ReturnsForbidden() {
        UUID articleId = testDataLoader.getArticle1().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/article/" + articleId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(articleJpaRepository.findById(articleId)).isPresent();
    }

    @Test
    void adminDeleteArticle_WithoutToken_ReturnsForbidden() {
        UUID articleId = testDataLoader.getArticle1().getId();
        HttpEntity<Void> request = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/article/" + articleId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void adminDeleteArticle_WithNonExistentArticle_ReturnsNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/article/" + nonExistentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void adminDeleteArticleFromAnotherAuthor_WithValidAdminToken_ReturnsNoContent() {
        UUID articleId = testDataLoader.getArticle2().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/article/" + articleId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(articleJpaRepository.findById(articleId)).isEmpty();
    }

    @Test
    void adminDeleteAuthor_WithValidAdminToken_ReturnsNoContent() {
        UUID authorId = testDataLoader.getAuthor3().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/author/" + authorId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(authorJpaRepository.findById(authorId)).isEmpty();
    }

    @Test
    void adminDeleteAuthor_WithAuthorToken_ReturnsForbidden() {
        UUID authorId = testDataLoader.getAuthor2().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/author/" + authorId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(authorJpaRepository.findById(authorId)).isPresent();
    }

    @Test
    void adminDeleteAuthor_WithNonExistentAuthor_ReturnsNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/author/" + nonExistentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void adminDeleteCommentWithoutReplies_WithValidAdminToken_HardDeletes() {
        UUID commentId = testDataLoader.getArticleComment2().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + commentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(commentJpaRepository.findById(commentId)).isEmpty();
    }

    @Test
    void adminDeleteCommentWithReplies_WithValidAdminToken_SoftDeletes() {
        UUID commentId = testDataLoader.getArticleComment1().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + commentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        var comment = commentJpaRepository.findById(commentId);
        assertThat(comment).isPresent();
        assertThat(comment.get().isDeleted()).isTrue();
    }

    @Test
    void adminDeleteComment_WithAuthorToken_ReturnsForbidden() {
        UUID commentId = testDataLoader.getArticleComment2().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + commentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(commentJpaRepository.findById(commentId)).isPresent();
    }

    @Test
    void adminDeleteComment_WithNonExistentComment_ReturnsNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + nonExistentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void adminDeleteCommentReply_WithValidAdminToken_ReturnsNoContent() {
        UUID replyId = testDataLoader.getArticleComment1Reply().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + replyId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(commentJpaRepository.findById(replyId)).isEmpty();
    }

    @Test
    void adminDeleteTopic_WithValidAdminToken_ReturnsNoContent() {
        UUID topicId = testDataLoader.getTopic1().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/topic/" + topicId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(topicJpaRepository.findById(topicId)).isEmpty();
    }

    @Test
    void adminDeleteTopic_WithAuthorToken_ReturnsForbidden() {
        UUID topicId = testDataLoader.getTopic1().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/topic/" + topicId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(topicJpaRepository.findById(topicId)).isPresent();
    }

    @Test
    void adminDeleteTopic_WithNonExistentTopic_ReturnsNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/topic/" + nonExistentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void adminDeleteTopicFromAnotherAuthor_WithValidAdminToken_ReturnsNoContent() {
        UUID topicId = testDataLoader.getTopic2().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/topic/" + topicId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(topicJpaRepository.findById(topicId)).isEmpty();
    }

    @Test
    void adminDeleteTopicCommentWithoutReplies_WithValidAdminToken_HardDeletes() {
        UUID commentId = testDataLoader.getTopicComment2().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + commentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(commentJpaRepository.findById(commentId)).isEmpty();
    }

    @Test
    void adminDeleteTopicCommentWithReplies_WithValidAdminToken_SoftDeletes() {
        UUID commentId = testDataLoader.getTopicComment1().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + commentId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        var comment = commentJpaRepository.findById(commentId);
        assertThat(comment).isPresent();
        assertThat(comment.get().isDeleted()).isTrue();
    }

    @Test
    void adminDeleteTopicCommentReply_WithValidAdminToken_ReturnsNoContent() {
        UUID replyId = testDataLoader.getTopicComment1Reply().getId();
        HttpHeaders headers = createAuthHeaders(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/comment/" + replyId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(commentJpaRepository.findById(replyId)).isEmpty();
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "token=" + token);
        return headers;
    }
}
