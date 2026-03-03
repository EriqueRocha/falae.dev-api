package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.TopicJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JpaTopicRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JpaTopicRepository jpaTopicRepository;

    @Autowired
    private TopicJpaRepository topicJpaRepository;

    @Autowired
    private TestDataLoader testDataLoader;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
    }

    @Test
    void adminDeleteById_WithExistingTopic_DeletesSuccessfully() {
        UUID topicId = testDataLoader.getTopic1().getId();

        jpaTopicRepository.adminDeleteById(topicId);

        assertThat(topicJpaRepository.findById(topicId)).isEmpty();
    }

    @Test
    void adminDeleteById_WithNonExistentTopic_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaTopicRepository.adminDeleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void adminDeleteById_WithTopicFromAnotherAuthor_DeletesSuccessfully() {
        UUID topicId = testDataLoader.getTopic2().getId();

        jpaTopicRepository.adminDeleteById(topicId);

        assertThat(topicJpaRepository.findById(topicId)).isEmpty();
    }

    @Test
    void findById_WithExistingTopic_ReturnsTopic() {
        UUID topicId = testDataLoader.getTopic1().getId();

        var topic = jpaTopicRepository.findById(topicId);

        assertThat(topic).isNotNull();
        assertThat(topic.getId()).isEqualTo(topicId);
        assertThat(topic.getTitle()).isEqualTo("First Topic Title");
    }

    @Test
    void findById_WithNonExistentTopic_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaTopicRepository.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}