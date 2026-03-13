package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.ports.dto.AuthorInteractionPageResponse;
import dev.falae.application.ports.dto.AuthorInteractionResponse;
import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.core.domain.entities.AuthorInteraction;
import dev.falae.core.domain.valueobjects.InteractionType;
import dev.falae.core.domain.valueobjects.TargetType;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorInteractionEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.AuthorInteractionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaAuthorInteractionRepository implements AuthorInteractionRepository {

    private final AuthorInteractionJpaRepository jpaRepository;

    public JpaAuthorInteractionRepository(AuthorInteractionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void save(AuthorInteraction interaction) {
        // Check if interaction already exists (upsert logic handled by unique constraint)
        var existing = jpaRepository.findByActorAuthorIdAndInteractionTypeAndTargetId(
                interaction.getActorAuthorId(),
                interaction.getInteractionType().name(),
                interaction.getTargetId()
        );

        if (existing.isEmpty()) {
            AuthorInteractionEntity entity = toEntity(interaction);
            jpaRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void delete(UUID actorAuthorId, InteractionType interactionType, UUID targetId) {
        jpaRepository.deleteByActorAuthorIdAndInteractionTypeAndTargetId(
                actorAuthorId, interactionType.name(), targetId);
    }

    @Override
    public AuthorInteractionPageResponse findByRecipientAuthorId(UUID recipientAuthorId, int page, int size) {
        Page<AuthorInteractionEntity> pageResult = jpaRepository.findByRecipientAuthorIdOrderByCreatedAtDesc(
                recipientAuthorId, PageRequest.of(page, size));

        List<AuthorInteractionResponse> interactions = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new AuthorInteractionPageResponse(
                interactions,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.hasNext()
        );
    }

    @Override
    public long countUnreadByRecipientAuthorId(UUID recipientAuthorId) {
        return jpaRepository.countUnreadByRecipientAuthorId(recipientAuthorId);
    }

    @Override
    @Transactional
    public void markAsRead(UUID interactionId, UUID recipientAuthorId) {
        jpaRepository.markAsRead(interactionId, recipientAuthorId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID recipientAuthorId) {
        jpaRepository.markAllAsRead(recipientAuthorId);
    }

    private AuthorInteractionEntity toEntity(AuthorInteraction interaction) {
        AuthorInteractionEntity entity = new AuthorInteractionEntity();
        entity.setId(interaction.getId());
        entity.setRecipientAuthorId(interaction.getRecipientAuthorId());
        entity.setActorAuthorId(interaction.getActorAuthorId());
        entity.setInteractionType(interaction.getInteractionType().name());
        entity.setTargetType(interaction.getTargetType().name());
        entity.setTargetId(interaction.getTargetId());
        entity.setTargetTitle(interaction.getTargetTitle());
        entity.setTargetSlug(interaction.getTargetSlug());
        entity.setCommentId(interaction.getCommentId());
        entity.setParentContentType(interaction.getParentContentType() != null ? interaction.getParentContentType().name() : null);
        entity.setParentAuthorUserName(interaction.getParentAuthorUserName());
        entity.setRead(interaction.isRead());
        entity.setCreatedAt(interaction.getCreatedAt());
        return entity;
    }

    private AuthorInteractionResponse toResponse(AuthorInteractionEntity entity) {
        return new AuthorInteractionResponse(
                entity.getId(),
                entity.getActorAuthorId(),
                entity.getActor() != null ? entity.getActor().getName() : null,
                entity.getActor() != null ? entity.getActor().getUserName() : null,
                entity.getActor() != null ? entity.getActor().getProfileImageUrl() : null,
                InteractionType.valueOf(entity.getInteractionType()),
                TargetType.valueOf(entity.getTargetType()),
                entity.getTargetId(),
                entity.getTargetTitle(),
                entity.getTargetSlug(),
                entity.getCommentId(),
                entity.getParentContentType() != null ? TargetType.valueOf(entity.getParentContentType()) : null,
                entity.getParentAuthorUserName(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
