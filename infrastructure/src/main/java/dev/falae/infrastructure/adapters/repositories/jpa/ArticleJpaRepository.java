package dev.falae.infrastructure.adapters.repositories.jpa;

import dev.falae.infrastructure.adapters.repositories.entities.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, UUID> {
    Page<ArticleEntity> findByIsPrivateFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<ArticleEntity> findByIsPrivateFalseOrderByCreatedAtAsc(Pageable pageable);
    Page<ArticleEntity> findByIsPrivateFalseOrderByLikesCountDesc(Pageable pageable);

    Page<ArticleEntity> findByAuthorUserNameAndIsPrivateFalseOrderByCreatedAtDesc(String userName, Pageable pageable);
    Page<ArticleEntity> findByAuthorUserNameAndIsPrivateFalseOrderByCreatedAtAsc(String userName, Pageable pageable);
    Page<ArticleEntity> findByAuthorUserNameAndIsPrivateFalseOrderByLikesCountDesc(String userName, Pageable pageable);
    Page<ArticleEntity> findByAuthorUserNameAndIsPrivateFalseOrderBySavesCountDesc(String userName, Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a WHERE a.author.userName = :userName AND a.isPrivate = false ORDER BY SIZE(a.comments) DESC, a.createdAt DESC")
    Page<ArticleEntity> findByAuthorUserNameAndIsPrivateFalseOrderByCommentsCountDesc(@Param("userName") String userName, Pageable pageable);

    Page<ArticleEntity> findByAuthorIdAndIsPrivateTrueOrderByCreatedAtDesc(UUID authorId, Pageable pageable);
    Page<ArticleEntity> findByAuthorIdAndIsPrivateTrueOrderByCreatedAtAsc(UUID authorId, Pageable pageable);
    Page<ArticleEntity> findByAuthorIdAndIsPrivateTrueOrderByLikesCountDesc(UUID authorId, Pageable pageable);
    Page<ArticleEntity> findByAuthorIdAndIsPrivateTrueOrderBySavesCountDesc(UUID authorId, Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a WHERE a.author.id = :authorId AND a.isPrivate = true ORDER BY SIZE(a.comments) DESC, a.createdAt DESC")
    Page<ArticleEntity> findByAuthorIdAndIsPrivateTrueOrderByCommentsCountDesc(@Param("authorId") UUID authorId, Pageable pageable);

    long countByAuthorId(UUID authorId);
    long countByAuthorIdAndIsPrivateFalse(UUID authorId);

    boolean existsByAuthorIdAndTitle(UUID authorId, String title);
    boolean existsByAuthorIdAndTitleAndIdNot(UUID authorId, String title, UUID excludeId);

    Page<ArticleEntity> findByTitleContainingIgnoreCaseAndIsPrivateFalseOrderByCreatedAtDesc(String title, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseAndIsPrivateFalseOrderByCreatedAtAsc(String title, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseAndIsPrivateFalseOrderByLikesCountDesc(String title, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseAndIsPrivateFalseOrderBySavesCountDesc(String title, Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')) AND a.isPrivate = false ORDER BY SIZE(a.comments) DESC, a.createdAt DESC")
    Page<ArticleEntity> findByTitleContainingIgnoreCaseAndIsPrivateFalseOrderByCommentsCountDesc(@Param("title") String title, Pageable pageable);

    Optional<ArticleEntity> findByAuthorUserNameAndSlugAndIsPrivateFalse(String userName, String slug);
}
