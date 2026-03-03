package dev.falae.application.ports.dto;

import java.util.List;

public record TagSearchPageResponse(
        List<TagSearchResultItem> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
}
