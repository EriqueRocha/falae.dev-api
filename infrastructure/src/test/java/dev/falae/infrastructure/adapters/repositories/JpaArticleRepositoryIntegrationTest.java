package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.jpa.ArticleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JpaArticleRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JpaArticleRepository jpaArticleRepository;

    @Autowired
    private ArticleJpaRepository articleJpaRepository;

    @Autowired
    private TestDataLoader testDataLoader;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
    }

    @Test
    void adminDeleteById_WithExistingArticle_DeletesSuccessfully() {
        UUID articleId = testDataLoader.getArticle1().getId();

        jpaArticleRepository.adminDeleteById(articleId);

        assertThat(articleJpaRepository.findById(articleId)).isEmpty();
    }

    @Test
    void adminDeleteById_WithNonExistentArticle_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaArticleRepository.adminDeleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void adminDeleteById_WithArticleFromAnotherAuthor_DeletesSuccessfully() {
        UUID articleId = testDataLoader.getArticle2().getId();

        jpaArticleRepository.adminDeleteById(articleId);

        assertThat(articleJpaRepository.findById(articleId)).isEmpty();
    }

    @Test
    void findById_WithExistingArticle_ReturnsArticle() {
        UUID articleId = testDataLoader.getArticle1().getId();

        var article = jpaArticleRepository.findById(articleId);

        assertThat(article).isNotNull();
        assertThat(article.getId()).isEqualTo(articleId);
        assertThat(article.getTitle()).isEqualTo("First Article Title");
    }

    @Test
    void findById_WithNonExistentArticle_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaArticleRepository.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}