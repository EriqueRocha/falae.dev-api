package dev.falae.application.ports.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TagSearchResultItem(
        UUID id,
        SearchContentType type,
        String title,
        String slug,
        String content,
        UUID authorId,
        String authorName,
        String authorUserName,
        LocalDateTime createdAt,
        int likesCount,
        boolean isLiked,
        String coverImage,
        List<String> tags,
        SearchContentType parentType,
        String parentAuthorUserName,
        String parentSlug
) {
}
