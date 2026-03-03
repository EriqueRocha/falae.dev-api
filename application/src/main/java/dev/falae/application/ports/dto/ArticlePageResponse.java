package dev.falae.application.ports.dto;

import java.util.List;

public record ArticlePageResponse(
        List<ArticleResponse> articles,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {}
