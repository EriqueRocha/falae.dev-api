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
            // Get the title, slug, parent content type and author username from the parent article or topic
            String targetTitle = null;
            String targetSlug = null;
            TargetType parentContentType = null;
            String parentAuthorUserName = null;
            if (comment.getArticle() != null) {
                targetTitle = comment.getArticle().getTitle();
                targetSlug = comment.getArticle().getSlug();
                parentContentType = TargetType.ARTICLE;
                if (comment.getArticle().getAuthor() != null) {
                    parentAuthorUserName = comment.getArticle().getAuthor().getUserName();
                }
            } else if (comment.getTopic() != null) {
                targetTitle = comment.getTopic().getTitle();
                targetSlug = comment.getTopic().getSlug();
                parentContentType = TargetType.TOPIC;
                if (comment.getTopic().getAuthor() != null) {
                    parentAuthorUserName = comment.getTopic().getAuthor().getUserName();
                }
            }

            if (isNowLiked) {
                AuthorInteraction interaction = AuthorInteraction.create(
                        comment.getAuthor().getId(),
                        currentAuthor.getId(),
                        InteractionType.LIKE_COMMENT,
                        TargetType.COMMENT,
                        comment.getId(),
                        targetTitle,
                        targetSlug,
                        comment.getId(),
                        parentContentType,
                        parentAuthorUserName
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
