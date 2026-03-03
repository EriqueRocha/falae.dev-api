package dev.falae.application.usecases;

import dev.falae.application.ports.dto.InteractionResponse;
import dev.falae.application.ports.repositories.CommentRepository;

import java.util.UUID;

public class CommentInteractionUseCase {

    private final CommentRepository commentRepository;

    public CommentInteractionUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public InteractionResponse toggleLike(UUID commentId) {
        boolean isNowLiked = commentRepository.toggleLike(commentId);
        return new InteractionResponse(isNowLiked, isNowLiked ? "Comment liked" : "Comment unliked");
    }

    public InteractionResponse toggleDislike(UUID commentId) {
        boolean isNowDisliked = commentRepository.toggleDislike(commentId);
        return new InteractionResponse(isNowDisliked, isNowDisliked ? "Comment disliked" : "Comment undisliked");
    }
}
