package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.exceptions.ValidationException;
import dev.falae.application.ports.dto.CreateCommentRequest;
import dev.falae.application.ports.dto.CreateCommentResponse;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.CommentRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.application.ports.repositories.TopicRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Article;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.Comment;
import dev.falae.core.domain.entities.ForumConfig;
import dev.falae.core.domain.entities.Topic;

public class CreateCommentUseCase {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final ForumConfigRepository forumConfigRepository;
    private final AuthorRepository authorRepository;
    private final AuthenticationService authenticationService;

    public CreateCommentUseCase(CommentRepository commentRepository,
                                 ArticleRepository articleRepository,
                                 TopicRepository topicRepository,
                                 ForumConfigRepository forumConfigRepository,
                                 AuthorRepository authorRepository,
                                 AuthenticationService authenticationService) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.forumConfigRepository = forumConfigRepository;
        this.authorRepository = authorRepository;
        this.authenticationService = authenticationService;
    }

    public CreateCommentResponse create(CreateCommentRequest request) {
        if (request.articleId() == null && request.topicId() == null) {
            throw new ValidationException("You must provide either articleId or topicId");
        }

        ForumConfig config = forumConfigRepository.getConfig();
        if (!config.isCommentUnlocked()) {
            throw new BusinessRuleException("Comments are currently disabled");
        }

        if (config.isEmailVerificationRequired()) {
            Author currentAuthor = authenticationService.getCurrentAuthor();
            if (!currentAuthor.isEmailVerified()) {
                throw new BusinessRuleException("You must verify your email before commenting");
            }
        }

        Comment comment;

        if (request.articleId() != null) {
            Article article = articleRepository.findById(request.articleId());
            if (article == null) {
                throw new ResourceNotFoundException("Article", request.articleId());
            }
            comment = Comment.forArticle(request.message(), article, request.parentId(), request.tags());
        } else {
            Topic topic = topicRepository.findById(request.topicId());
            if (topic == null) {
                throw new ResourceNotFoundException("Topic", request.topicId());
            }
            comment = Comment.forTopic(request.message(), topic, request.parentId(), request.tags());
        }

        Comment savedComment = commentRepository.save(comment);

        authorRepository.addCoinsToCurrentAuthor(config.getCoinsPerComment());

        return new CreateCommentResponse(
                "Comment created successfully",
                savedComment.getId(),
                savedComment.getCommentContent()
        );
    }
}
