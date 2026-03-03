package dev.falae.application.ports.dto;

import java.util.List;

public record CommentPageResponse(
        List<CommentResponse> comments,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {}
