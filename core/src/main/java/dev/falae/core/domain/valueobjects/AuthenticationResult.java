package dev.falae.core.domain.valueobjects;

public record AuthenticationResult(String token, String role, String email, String name) {
}
