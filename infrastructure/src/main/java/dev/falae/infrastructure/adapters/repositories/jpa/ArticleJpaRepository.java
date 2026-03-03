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
    Page<ArticleEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<ArticleEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);
    Page<ArticleEntity> findAllByOrderByLikesCountDesc(Pageable pageable);

    Page<ArticleEntity> findByAuthorUserNameOrderByCreatedAtDesc(String userName, Pageable pageable);
    Page<ArticleEntity> findByAuthorUserNameOrderByCreatedAtAsc(String userName, Pageable pageable);
    Page<ArticleEntity> findByAuthorUserNameOrderByLikesCountDesc(String userName, Pageable pageable);
    Page<ArticleEntity> findByAuthorUserNameOrderBySavesCountDesc(String userName, Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a WHERE a.author.userName = :userName ORDER BY SIZE(a.comments) DESC, a.createdAt DESC")
    Page<ArticleEntity> findByAuthorUserNameOrderByCommentsCountDesc(@Param("userName") String userName, Pageable pageable);

    long countByAuthorId(UUID authorId);

    boolean existsByAuthorIdAndTitle(UUID authorId, String title);
    boolean existsByAuthorIdAndTitleAndIdNot(UUID authorId, String title, UUID excludeId);

    Page<ArticleEntity> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseOrderByCreatedAtAsc(String title, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseOrderByLikesCountDesc(String title, Pageable pageable);
    Page<ArticleEntity> findByTitleContainingIgnoreCaseOrderBySavesCountDesc(String title, Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY SIZE(a.comments) DESC, a.createdAt DESC")
    Page<ArticleEntity> findByTitleContainingIgnoreCaseOrderByCommentsCountDesc(@Param("title") String title, Pageable pageable);

    Optional<ArticleEntity> findByAuthorUserNameAndSlug(String userName, String slug);
}
