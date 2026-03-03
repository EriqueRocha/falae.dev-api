package dev.falae.application.ports.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        String authorName,
        String authorUserName,
        String authorProfileImage,
        UUID authorId,
        LocalDateTime createdAt,
        int likes,
        int dislikes,
        int depth,
        long replyCount,
        UUID parentId,
        UUID articleId,
        UUID topicId,
        boolean deleted,
        String parentAuthorUserName,
        String parentTitle,
        String parentSlug,
        Boolean isLiked,
        Boolean isDisliked,
        Boolean isOwner,
        List<String> tags
) {}
