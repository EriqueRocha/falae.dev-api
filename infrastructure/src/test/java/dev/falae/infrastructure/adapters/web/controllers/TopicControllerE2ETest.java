package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.CreateTopicRequest;
import dev.falae.application.ports.dto.CreateTopicResponse;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.TopicJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TopicControllerE2ETest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataLoader testDataLoader;

    @Autowired
    private TopicJpaRepository topicJpaRepository;

    private String baseUrl;
    private String authorToken;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
        baseUrl = "http://localhost:" + port + "/api/topic";
        authorToken = testDataLoader.generateAuthorToken(testDataLoader.getAuthor1());
    }

    @Test
    void saveTopic_WithValidData_ReturnsCreated() {
        CreateTopicRequest request = new CreateTopicRequest(
                "New Topic Title",
                "Content of the new topic discussing important matters",
                List.of("java", "testing")
        );
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<CreateTopicRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<CreateTopicResponse> response = restTemplate.exchange(
                baseUrl + "/saveNew",
                HttpMethod.POST,
                entity,
                CreateTopicResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().slug()).isEqualTo("new-topic-title");
        assertThat(topicJpaRepository.findById(response.getBody().id())).isPresent();
    }

    @Test
    void saveTopic_WithoutToken_ReturnsForbidden() {
        CreateTopicRequest request = new CreateTopicRequest(
                "New Topic Title",
                "Content of the new topic",
                null
        );
        HttpEntity<CreateTopicRequest> entity = new HttpEntity<>(request);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/saveNew",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteTopic_WithOwnerToken_ReturnsNoContent() {
        UUID topicId = testDataLoader.getTopic1().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/delete/" + topicId,
                HttpMethod.DELETE,
                entity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(topicJpaRepository.findById(topicId)).isEmpty();
    }

    @Test
    void deleteTopic_WithNonOwnerToken_ReturnsNotFound() {
        UUID topicId = testDataLoader.getTopic2().getId();
        HttpHeaders headers = createAuthHeaders(authorToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/delete/" + topicId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(topicJpaRepository.findById(topicId)).isPresent();
    }

    @Test
    void deleteTopic_WithNonExistentId_ReturnsNotFound() {
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
    void deleteTopic_WithoutToken_ReturnsForbidden() {
        UUID topicId = testDataLoader.getTopic1().getId();
        HttpEntity<Void> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/delete/" + topicId,
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