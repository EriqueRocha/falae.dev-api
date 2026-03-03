package dev.falae.application.ports.dto;

public record ForumStatsResponse(
        long totalArticles,
        long totalTopics,
        long totalComments
) {}
