package dev.falae.application.ports.dto;

public record VerifyEmailResponse(
        String message,
        String userName,
        String token
) {}
