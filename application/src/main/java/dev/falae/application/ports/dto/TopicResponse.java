package dev.falae.application.ports.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TopicResponse(
        UUID id,
        UUID authorId,
        String authorName,
        String authorUserName,
        String authorProfileImage,
        LocalDateTime creationDate,
        String title,
        String slug,
        String topicContent,
        List<String> tags,
        int likesCount,
        int dislikesCount,
        int commentsCount,
        Boolean isLiked,
        Boolean isDisliked
) {}
