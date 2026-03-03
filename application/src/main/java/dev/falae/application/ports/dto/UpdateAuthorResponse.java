package dev.falae.application.ports.dto;

public record UpdateAuthorResponse(
        String message,
        String name,
        String gitHub,
        String bio
) {}
