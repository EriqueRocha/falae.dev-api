package dev.falae.application.ports.dto;

import java.util.UUID;

public record CreateAuthorResponse(
        String message,
        UUID id,
        String email,
        String name
) {}
