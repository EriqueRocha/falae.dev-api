package dev.falae.infrastructure.adapters.repositories.jpa;

import dev.falae.infrastructure.adapters.repositories.entities.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TopicJpaRepository extends JpaRepository<TopicEntity, UUID> {
    Page<TopicEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<TopicEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);
    Page<TopicEntity> findAllByOrderByLikesCountDesc(Pageable pageable);

    Page<TopicEntity> findByAuthorUserNameOrderByCreatedAtDesc(String userName, Pageable pageable);
    Page<TopicEntity> findByAuthorUserNameOrderByCreatedAtAsc(String userName, Pageable pageable);
    Page<TopicEntity> findByAuthorUserNameOrderByLikesCountDesc(String userName, Pageable pageable);

    @Query("SELECT t FROM TopicEntity t WHERE t.author.userName = :userName ORDER BY SIZE(t.comments) DESC, t.createdAt DESC")
    Page<TopicEntity> findByAuthorUserNameOrderByCommentsCountDesc(@Param("userName") String userName, Pageable pageable);

    long countByAuthorId(UUID authorId);

    boolean existsByAuthorIdAndTitle(UUID authorId, String title);
    boolean existsByAuthorIdAndTitleAndIdNot(UUID authorId, String title, UUID topicId);

    java.util.Optional<TopicEntity> findByAuthorUserNameAndSlug(String userName, String slug);

    Page<TopicEntity> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);
    Page<TopicEntity> findByTitleContainingIgnoreCaseOrderByCreatedAtAsc(String title, Pageable pageable);
    Page<TopicEntity> findByTitleContainingIgnoreCaseOrderByLikesCountDesc(String title, Pageable pageable);

    @Query("SELECT t FROM TopicEntity t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY SIZE(t.comments) DESC, t.createdAt DESC")
    Page<TopicEntity> findByTitleContainingIgnoreCaseOrderByCommentsCountDesc(@Param("title") String title, Pageable pageable);
}
