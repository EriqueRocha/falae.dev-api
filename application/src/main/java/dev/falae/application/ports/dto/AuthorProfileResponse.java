package dev.falae.application.ports.dto;

import java.util.UUID;

public record AuthorProfileResponse(
        UUID id,
        String name,
        String userName,
        String gitHub,
        String profileImageUrl,
        String bio,
        int bugCoins,
        String title,
        long articleCount,
        long topicCount,
        long commentCount,
        boolean emailVerified
) {}