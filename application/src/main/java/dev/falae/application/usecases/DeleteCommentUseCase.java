package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.CommentRepository;

import java.util.UUID;

public class DeleteCommentUseCase {

    private final CommentRepository commentRepository;

    public DeleteCommentUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void delete(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

}