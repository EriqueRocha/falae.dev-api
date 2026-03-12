package dev.falae.application.usecases;

import dev.falae.application.ports.dto.InteractionResponse;
import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.repositories.CommentRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.AuthorInteraction;
import dev.falae.core.domain.entities.Comment;
import dev.falae.core.domain.valueobjects.InteractionType;
import dev.falae.core.domain.valueobjects.TargetType;

import java.util.UUID;

public class CommentInteractionUseCase {

    private final CommentRepository commentRepository;
    private final AuthorInteractionRepository authorInteractionRepository;
    private final AuthenticationService authenticationService;

    public CommentInteractionUseCase(CommentRepository commentRepository,
                                      AuthorInteractionRepository authorInteractionRepository,
                                      AuthenticationService authenticationService) {
        this.commentRepository = commentRepository;
        this.authorInteractionRepository = authorInteractionRepository;
        this.authenticationService = authenticationService;
    }

    public InteractionResponse toggleLike(UUID commentId) {
        boolean isNowLiked = commentRepository.toggleLike(commentId);

        Author currentAuthor = authenticationService.getCurrentAuthor();
        Comment comment = commentRepository.findById(commentId);

        if (comment != null && comment.getAuthor() != null
                && !comment.getAuthor().getId().equals(currentAuthor.getId())) {
            // Get the title and slug from the parent article or topic
            String targetTitle = null;
            String targetSlug = null;
            if (comment.getArticle() != null) {
                targetTitle = comment.getArticle().getTitle();
                targetSlug = comment.getArticle().getSlug();
            } else if (comment.getTopic() != null) {
                targetTitle = comment.getTopic().getTitle();
                targetSlug = comment.getTopic().getSlug();
            }

            if (isNowLiked) {
                AuthorInteraction interaction = AuthorInteraction.create(
                        comment.getAuthor().getId(),
                        currentAuthor.getId(),
                        InteractionType.LIKE_COMMENT,
                        TargetType.COMMENT,
                        comment.getId(),
                        targetTitle,
                        targetSlug
                );
                authorInteractionRepository.save(interaction);
            } else {
                authorInteractionRepository.delete(currentAuthor.getId(), InteractionType.LIKE_COMMENT, commentId);
            }
        }

        return new InteractionResponse(isNowLiked, isNowLiked ? "Comment liked" : "Comment unliked");
    }

    public InteractionResponse toggleDislike(UUID commentId) {
        boolean isNowDisliked = commentRepository.toggleDislike(commentId);
        return new InteractionResponse(isNowDisliked, isNowDisliked ? "Comment disliked" : "Comment undisliked");
    }
}
