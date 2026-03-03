package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.AuthorJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JpaAuthorRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;

    @Autowired
    private AuthorJpaRepository authorJpaRepository;

    @Autowired
    private TestDataLoader testDataLoader;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
    }

    @Test
    void deleteById_WithAuthorWithoutDependencies_DeletesSuccessfully() {
        UUID authorId = testDataLoader.getAuthor3().getId();

        jpaAuthorRepository.deleteById(authorId);

        assertThat(authorJpaRepository.findById(authorId)).isEmpty();
    }

    @Test
    void deleteById_WithNonExistentAuthor_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaAuthorRepository.deleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_WithExistingAuthor_ReturnsAuthor() {
        UUID authorId = testDataLoader.getAuthor1().getId();

        var author = jpaAuthorRepository.findById(authorId);

        assertThat(author).isNotNull();
        assertThat(author.getId()).isEqualTo(authorId);
        assertThat(author.getName()).isEqualTo("Author One");
        assertThat(author.getEmail()).isEqualTo("author1@test.com");
    }

    @Test
    void findById_WithNonExistentAuthor_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaAuthorRepository.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByEmail_WithExistingEmail_ReturnsAuthor() {
        var author = jpaAuthorRepository.findByEmail("author1@test.com");

        assertThat(author).isNotNull();
        assertThat(author.getEmail()).isEqualTo("author1@test.com");
        assertThat(author.getName()).isEqualTo("Author One");
    }

    @Test
    void findByEmail_WithNonExistentEmail_ThrowsResourceNotFoundException() {
        assertThatThrownBy(() -> jpaAuthorRepository.findByEmail("nonexistent@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}