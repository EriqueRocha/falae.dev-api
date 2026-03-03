package dev.falae.application.ports.dto;

import java.util.List;

public record FeedPageResponse(
        List<FeedItemResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {}
