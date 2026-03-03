package dev.falae.application.ports.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ArticleResponse(
        UUID id,
        UUID authorId,
        String authorName,
        String authorUserName,
        String authorProfileImage,
        LocalDateTime creationDate,
        Boolean isMarkdown,
        String title,
        String slug,
        String coverImage,
        String originalPost,
        List<String> tags,
        String description,
        String urlArticleContent,
        int likesCount,
        int dislikesCount,
        int commentsCount,
        int savesCount,
        Boolean isLiked,
        Boolean isDisliked,
        Boolean isSaved
) {}
