package dev.falae.application.ports.repositories;

import dev.falae.application.ports.dto.AuthorInteractionPageResponse;
import dev.falae.core.domain.entities.AuthorInteraction;
import dev.falae.core.domain.valueobjects.InteractionType;

import java.util.UUID;

public interface AuthorInteractionRepository {

    void save(AuthorInteraction interaction);

    void delete(UUID actorAuthorId, InteractionType interactionType, UUID targetId);

    AuthorInteractionPageResponse findByRecipientAuthorId(UUID recipientAuthorId, int page, int size);

    long countUnreadByRecipientAuthorId(UUID recipientAuthorId);

    void markAsRead(UUID interactionId, UUID recipientAuthorId);

    void markAllAsRead(UUID recipientAuthorId);
}
