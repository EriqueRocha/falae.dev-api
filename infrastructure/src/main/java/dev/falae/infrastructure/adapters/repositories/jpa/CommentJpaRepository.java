package dev.falae.infrastructure.adapters.repositories.jpa;

import dev.falae.infrastructure.adapters.repositories.entities.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, UUID> {

    @Query("SELECT c FROM CommentEntity c WHERE c.article.id = :articleId ORDER BY c.path")
    List<CommentEntity> findByArticleIdOrderByPath(@Param("articleId") UUID articleId);

    @Query("SELECT c FROM CommentEntity c WHERE c.topic.id = :topicId ORDER BY c.path")
    List<CommentEntity> findByTopicIdOrderByPath(@Param("topicId") UUID topicId);

    @Query("SELECT c FROM CommentEntity c WHERE c.article.id = :articleId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<CommentEntity> findRootCommentsByArticleId(@Param("articleId") UUID articleId, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.topic.id = :topicId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<CommentEntity> findRootCommentsByTopicId(@Param("topicId") UUID topicId, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.parent.id = :parentId ORDER BY c.createdAt ASC")
    List<CommentEntity> findDirectReplies(@Param("parentId") UUID parentId);

    @Query("SELECT c FROM CommentEntity c WHERE c.path LIKE :pathPrefix% ORDER BY c.path")
    List<CommentEntity> findSubtree(@Param("pathPrefix") String pathPrefix);

    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.parent.id = :parentId")
    long countDirectReplies(@Param("parentId") UUID parentId);

    @Query("SELECT c FROM CommentEntity c WHERE c.deleted = false ORDER BY c.createdAt DESC")
    Page<CommentEntity> findAllCommentsOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.deleted = false ORDER BY c.createdAt ASC")
    Page<CommentEntity> findAllCommentsOrderByCreatedAtAsc(Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.deleted = false ORDER BY c.likes DESC, c.createdAt DESC")
    Page<CommentEntity> findAllCommentsOrderByLikesDesc(Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.author.userName = :userName AND c.deleted = false ORDER BY c.createdAt DESC")
    Page<CommentEntity> findByAuthorUserNameOrderByCreatedAtDesc(@Param("userName") String userName, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.author.userName = :userName AND c.deleted = false ORDER BY c.createdAt ASC")
    Page<CommentEntity> findByAuthorUserNameOrderByCreatedAtAsc(@Param("userName") String userName, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.author.userName = :userName AND c.deleted = false ORDER BY c.likes DESC, c.createdAt DESC")
    Page<CommentEntity> findByAuthorUserNameOrderByLikesDesc(@Param("userName") String userName, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.author.id = :authorId AND c.deleted = false")
    long countByAuthorId(@Param("authorId") UUID authorId);
}
