package dev.falae.application.ports.dto;

import java.util.UUID;

public record CreateTopicResponse(
        UUID id,
        String title,
        String slug
) {
}
