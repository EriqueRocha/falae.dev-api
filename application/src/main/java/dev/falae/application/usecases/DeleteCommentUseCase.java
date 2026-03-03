package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.CommentRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.core.domain.entities.ForumConfig;

import java.util.UUID;

public class DeleteCommentUseCase {

    private final CommentRepository commentRepository;
    private final ForumConfigRepository forumConfigRepository;
    private final AuthorRepository authorRepository;

    public DeleteCommentUseCase(CommentRepository commentRepository,
                                ForumConfigRepository forumConfigRepository,
                                AuthorRepository authorRepository) {
        this.commentRepository = commentRepository;
        this.forumConfigRepository = forumConfigRepository;
        this.authorRepository = authorRepository;
    }

    public void delete(UUID commentId) {
        commentRepository.deleteById(commentId);

        ForumConfig config = forumConfigRepository.getConfig();
        authorRepository.removeCoinsFromCurrentAuthor(config.getCoinsPerComment());
    }

}