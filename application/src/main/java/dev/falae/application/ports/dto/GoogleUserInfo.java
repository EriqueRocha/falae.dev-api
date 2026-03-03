package dev.falae.application.ports.dto;

public record GoogleUserInfo(
    String email,
    String name,
    String pictureUrl,
    boolean emailVerified
) {
}
