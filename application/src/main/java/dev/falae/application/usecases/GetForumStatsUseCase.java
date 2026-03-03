package dev.falae.application.usecases;

import dev.falae.application.ports.dto.ForumStatsResponse;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.repositories.CommentRepository;
import dev.falae.application.ports.repositories.TopicRepository;

public class GetForumStatsUseCase {

    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;

    public GetForumStatsUseCase(ArticleRepository articleRepository,
                                 TopicRepository topicRepository,
                                 CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.commentRepository = commentRepository;
    }

    public ForumStatsResponse getStats() {
        return new ForumStatsResponse(
                articleRepository.count(),
                topicRepository.count(),
                commentRepository.count()
        );
    }
}
