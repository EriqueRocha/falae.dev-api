package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.CommentRepository;

import java.util.UUID;

public class AdminDeleteCommentUseCase {

    private final CommentRepository commentRepository;

    public AdminDeleteCommentUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void delete(UUID commentId) {
        commentRepository.adminDeleteById(commentId);
    }

}
