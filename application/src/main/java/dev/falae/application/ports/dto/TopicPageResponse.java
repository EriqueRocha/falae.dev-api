package dev.falae.application.ports.dto;

import java.util.List;

public record TopicPageResponse(
        List<TopicResponse> topics,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {}
