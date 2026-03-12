package dev.falae.infrastructure.adapters.repositories.jpa;

import dev.falae.infrastructure.adapters.repositories.entities.AuthorInteractionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AuthorInteractionJpaRepository extends JpaRepository<AuthorInteractionEntity, UUID> {

    @Query("SELECT i FROM AuthorInteractionEntity i JOIN FETCH i.actor WHERE i.recipientAuthorId = :recipientAuthorId ORDER BY i.createdAt DESC")
    Page<AuthorInteractionEntity> findByRecipientAuthorIdOrderByCreatedAtDesc(
            @Param("recipientAuthorId") UUID recipientAuthorId,
            Pageable pageable);

    @Query("SELECT COUNT(i) FROM AuthorInteractionEntity i WHERE i.recipientAuthorId = :recipientAuthorId AND i.isRead = false")
    long countUnreadByRecipientAuthorId(@Param("recipientAuthorId") UUID recipientAuthorId);

    @Modifying
    @Query("UPDATE AuthorInteractionEntity i SET i.isRead = true WHERE i.id = :id AND i.recipientAuthorId = :recipientAuthorId")
    int markAsRead(@Param("id") UUID id, @Param("recipientAuthorId") UUID recipientAuthorId);

    @Modifying
    @Query("UPDATE AuthorInteractionEntity i SET i.isRead = true WHERE i.recipientAuthorId = :recipientAuthorId AND i.isRead = false")
    int markAllAsRead(@Param("recipientAuthorId") UUID recipientAuthorId);

    Optional<AuthorInteractionEntity> findByActorAuthorIdAndInteractionTypeAndTargetId(
            UUID actorAuthorId, String interactionType, UUID targetId);

    void deleteByActorAuthorIdAndInteractionTypeAndTargetId(
            UUID actorAuthorId, String interactionType, UUID targetId);
}
