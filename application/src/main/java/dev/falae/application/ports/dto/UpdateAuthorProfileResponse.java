package dev.falae.application.ports.dto;

public record UpdateAuthorProfileResponse(
        String message,
        String email,
        String name,
        String userName,
        String gitHub,
        String profileImageUrl,
        String bio
) {}
