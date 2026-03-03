package dev.falae.application.ports.dto;

import java.util.UUID;

public record CreateArticleResponse(
        String message,
        UUID id,
        String title,
        String slug
) {
}
