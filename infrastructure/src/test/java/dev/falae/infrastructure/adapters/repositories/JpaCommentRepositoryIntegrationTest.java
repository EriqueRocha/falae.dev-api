package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.infrastructure.BaseIntegrationTest;
import dev.falae.infrastructure.TestDataLoader;
import dev.falae.infrastructure.adapters.repositories.entities.CommentEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.CommentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JpaCommentRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

    @Autowired
    private CommentJpaRepository commentJpaRepository;

    @Autowired
    private TestDataLoader testDataLoader;

    @BeforeEach
    void setUp() {
        testDataLoader.loadTestData();
    }

    @Test
    void adminDeleteById_WithCommentWithoutReplies_HardDeletesFromDatabase() {
        UUID commentId = testDataLoader.getArticleComment2().getId();

        jpaCommentRepository.adminDeleteById(commentId);

        assertThat(commentJpaRepository.findById(commentId)).isEmpty();
    }

    @Test
    void adminDeleteById_WithCommentWithReplies_SoftDeletesAndKeepsInDatabase() {
        UUID commentId = testDataLoader.getArticleComment1().getId();

        jpaCommentRepository.adminDeleteById(commentId);

        var comment = commentJpaRepository.findById(commentId);
        assertThat(comment).isPresent();
        assertThat(comment.get().isDeleted()).isTrue();
        assertThat(comment.get().getCommentContent()).isNull();
    }

    @Test
    void adminDeleteById_WithNonExistentComment_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaCommentRepository.adminDeleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void adminDeleteById_WithCommentReply_HardDeletesFromDatabase() {
        UUID replyId = testDataLoader.getArticleComment1Reply().getId();

        jpaCommentRepository.adminDeleteById(replyId);

        assertThat(commentJpaRepository.findById(replyId)).isEmpty();
    }

    @Test
    void adminDeleteById_WithTopicCommentWithoutReplies_HardDeletesFromDatabase() {
        UUID commentId = testDataLoader.getTopicComment2().getId();

        jpaCommentRepository.adminDeleteById(commentId);

        assertThat(commentJpaRepository.findById(commentId)).isEmpty();
    }

    @Test
    void adminDeleteById_WithTopicCommentWithReplies_SoftDeletesAndKeepsInDatabase() {
        UUID commentId = testDataLoader.getTopicComment1().getId();

        jpaCommentRepository.adminDeleteById(commentId);

        var comment = commentJpaRepository.findById(commentId);
        assertThat(comment).isPresent();
        assertThat(comment.get().isDeleted()).isTrue();
        assertThat(comment.get().getCommentContent()).isNull();
    }

    @Test
    void adminDeleteById_WithTopicCommentReply_HardDeletesFromDatabase() {
        UUID replyId = testDataLoader.getTopicComment1Reply().getId();

        jpaCommentRepository.adminDeleteById(replyId);

        assertThat(commentJpaRepository.findById(replyId)).isEmpty();
    }

    @Test
    void adminDeleteById_WithCommentFromAnotherAuthor_DeletesSuccessfully() {
        UUID commentId = testDataLoader.getArticleComment1().getId();

        jpaCommentRepository.adminDeleteById(commentId);

        var comment = commentJpaRepository.findById(commentId);
        assertThat(comment).isPresent();
        assertThat(comment.get().isDeleted()).isTrue();
    }

    @Test
    void findById_WithExistingArticleComment_ReturnsComment() {
        UUID commentId = testDataLoader.getArticleComment1().getId();

        var comment = jpaCommentRepository.findById(commentId);

        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getCommentContent()).isEqualTo("First comment on article 1");
    }

    @Test
    void findById_WithExistingTopicComment_ReturnsComment() {
        UUID commentId = testDataLoader.getTopicComment1().getId();

        var comment = jpaCommentRepository.findById(commentId);

        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getCommentContent()).isEqualTo("First comment on topic 1");
    }

    @Test
    void findById_WithNonExistentComment_ThrowsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> jpaCommentRepository.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void countReplies_WithCommentThatHasReplies_ReturnsCorrectCount() {
        UUID commentId = testDataLoader.getArticleComment1().getId();

        long count = jpaCommentRepository.countReplies(commentId);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void countReplies_WithCommentWithoutReplies_ReturnsZero() {
        UUID commentId = testDataLoader.getArticleComment2().getId();

        long count = jpaCommentRepository.countReplies(commentId);

        assertThat(count).isEqualTo(0);
    }

    @Test
    void countReplies_WithNestedThread_ReturnsAllDescendants() {
        CommentEntity root = testDataLoader.getArticleComment1();
        CommentEntity firstDirectReply = testDataLoader.getArticleComment1Reply();
        CommentEntity firstNestedReply = createReply(firstDirectReply, "First nested reply");
        createReply(firstDirectReply, "Second nested reply");
        createReply(root, "Second direct reply");

        assertThat(jpaCommentRepository.countReplies(root.getId())).isEqualTo(4);
        assertThat(jpaCommentRepository.countReplies(firstDirectReply.getId())).isEqualTo(2);
        assertThat(jpaCommentRepository.countReplies(firstNestedReply.getId())).isZero();
        assertThat(jpaCommentRepository.findCommentResponseById(root.getId()).replyCount()).isEqualTo(4);
        assertThat(jpaCommentRepository.findReplies(root.getId()))
                .hasSize(2)
                .allMatch(reply -> reply.parentId().equals(root.getId()));
    }

    private CommentEntity createReply(CommentEntity parent, String content) {
        CommentEntity reply = new CommentEntity();
        reply.setId(UUID.randomUUID());
        reply.setAuthor(testDataLoader.getAuthor1());
        reply.setArticle(parent.getArticle());
        reply.setTopic(parent.getTopic());
        reply.setParent(parent);
        reply.setCommentContent(content);
        reply.setDepth(parent.getDepth() + 1);
        reply.setPath(parent.getPath() + "/" + reply.getId());
        return commentJpaRepository.save(reply);
    }
}
