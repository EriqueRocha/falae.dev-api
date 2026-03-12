package dev.falae.application.ports.dto;

import dev.falae.core.domain.valueobjects.InteractionType;
import dev.falae.core.domain.valueobjects.TargetType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthorInteractionResponse(
        UUID id,
        UUID actorAuthorId,
        String actorName,
        String actorUserName,
        String actorProfileImageUrl,
        InteractionType interactionType,
        TargetType targetType,
        UUID targetId,
        String targetTitle,
        String targetSlug,
        boolean isRead,
        LocalDateTime createdAt
) {}
