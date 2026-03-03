package dev.falae.application.ports.dto;

public record LoginResponse(
        String message,
        String role,
        String email,
        String userName,
        String profileImageUrl,
        String name,
        String token,
        boolean emailVerified
) {}
