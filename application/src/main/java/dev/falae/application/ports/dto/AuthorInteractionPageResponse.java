package dev.falae.application.ports.dto;

import java.util.List;

public record AuthorInteractionPageResponse(
        List<AuthorInteractionResponse> interactions,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {}
