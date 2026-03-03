package dev.falae.application.ports.dto;

public record InteractionResponse(
        boolean isActive,
        String message
) {}
