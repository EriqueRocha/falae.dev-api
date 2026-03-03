package dev.falae.application.ports.dto;

import java.util.UUID;

public record CreateCommentResponse(
        String message,
        UUID id,
        String content
) {}
