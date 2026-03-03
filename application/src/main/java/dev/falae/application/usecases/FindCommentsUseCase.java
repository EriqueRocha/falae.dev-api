package dev.falae.application.usecases;

import dev.falae.application.ports.dto.CommentPageResponse;
import dev.falae.application.ports.dto.CommentResponse;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.repositories.CommentRepository;

import java.util.List;
import java.util.UUID;

public class FindCommentsUseCase {

    private final CommentRepository commentRepository;

    public FindCommentsUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentPageResponse findAll(int page, int size, FeedSortType sortType) {
        return commentRepository.findAll(page, size, sortType);
    }

    public CommentPageResponse findByArticleId(UUID articleId, int page, int size) {
        return commentRepository.findRootCommentsByArticleId(articleId, page, size);
    }

    public CommentPageResponse findByTopicId(UUID topicId, int page, int size) {
        return commentRepository.findRootCommentsByTopicId(topicId, page, size);
    }

    public List<CommentResponse> findReplies(UUID parentId) {
        return commentRepository.findReplies(parentId);
    }

    public CommentResponse findById(UUID commentId) {
        return commentRepository.findCommentResponseById(commentId);
    }
}
