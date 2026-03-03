package dev.falae.application.ports.services;

import java.util.UUID;

public interface TokenService {
    String generateToken(UUID id, String email, String role);
    boolean validateToken(String token);
    String extractEmail(String token);
    String extractRole(String token);
    UUID extractUserId(String token);

    String generateEmailVerificationToken(UUID userId, String email);
    boolean validateEmailVerificationToken(String token);
    UUID extractUserIdFromVerificationToken(String token);
}
