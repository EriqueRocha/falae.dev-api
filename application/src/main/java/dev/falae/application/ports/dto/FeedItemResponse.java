package dev.falae.application.ports.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeedItemResponse(
        UUID id,
        FeedItemType type,
        String title,
        String slug,
        String content,
        UUID authorId,
        String authorName,
        String authorUserName,
        String authorProfileImage,
        LocalDateTime createdAt,
        int likesCount,
        int dislikesCount,
        int commentsCount,
        int savesCount,
        String coverImage,
        List<String> tags,
        FeedItemType parentType,
        String parentAuthorUserName,
        String parentTitle,
        String parentSlug,
        Boolean isLiked,
        Boolean isDisliked,
        Boolean isSaved,
        Boolean isOwner
) {
    public enum FeedItemType {
        ARTICLE,
        TOPIC,
        COMMENT
    }
}
