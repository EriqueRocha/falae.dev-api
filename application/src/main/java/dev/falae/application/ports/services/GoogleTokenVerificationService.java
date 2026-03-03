package dev.falae.application.ports.services;

import dev.falae.application.ports.dto.GoogleUserInfo;

public interface GoogleTokenVerificationService {
    GoogleUserInfo verifyToken(String credential);
}
