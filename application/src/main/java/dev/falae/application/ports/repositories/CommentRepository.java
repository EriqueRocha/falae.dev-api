package dev.falae.application.ports.repositories;

import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.CommentPageResponse;
import dev.falae.application.ports.dto.CommentResponse;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.core.domain.entities.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentRepository {
    Comment save(Comment comment);
    Comment findById(UUID commentId);
    CommentResponse findCommentResponseById(UUID commentId);
    CommentPageResponse findAll(int page, int size, FeedSortType sortType);
    CommentPageResponse findByAuthorUserName(String userName, int page, int size, AuthorContentSortType sortType);
    long countByAuthorId(UUID authorId);
    CommentPageResponse findRootCommentsByArticleId(UUID articleId, int page, int size);
    CommentPageResponse findRootCommentsByTopicId(UUID topicId, int page, int size);
    List<CommentResponse> findReplies(UUID parentId);
    long countReplies(UUID parentId);
    void deleteById(UUID commentId);
    void adminDeleteById(UUID commentId);
    long count();
    boolean toggleLike(UUID commentId);
    boolean toggleDislike(UUID commentId);
}
