package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.exceptions.ValidationException;
import dev.falae.application.ports.dto.CreateCommentRequest;
import dev.falae.application.ports.dto.CreateCommentResponse;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.CommentRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.application.ports.repositories.TopicRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Article;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.AuthorInteraction;
import dev.falae.core.domain.entities.Comment;
import dev.falae.core.domain.entities.ForumConfig;
import dev.falae.core.domain.entities.Topic;
import dev.falae.core.domain.valueobjects.InteractionType;
import dev.falae.core.domain.valueobjects.TargetType;

public class CreateCommentUseCase {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final ForumConfigRepository forumConfigRepository;
    private final AuthorRepository authorRepository;
    private final AuthenticationService authenticationService;
    private final AuthorInteractionRepository authorInteractionRepository;

    public CreateCommentUseCase(CommentRepository commentRepository,
                                 ArticleRepository articleRepository,
                                 TopicRepository topicRepository,
                                 ForumConfigRepository forumConfigRepository,
                                 AuthorRepository authorRepository,
                                 AuthenticationService authenticationService,
                                 AuthorInteractionRepository authorInteractionRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.forumConfigRepository = forumConfigRepository;
        this.authorRepository = authorRepository;
        this.authenticationService = authenticationService;
        this.authorInteractionRepository = authorInteractionRepository;
    }

    public CreateCommentResponse create(CreateCommentRequest request) {
        if (request.articleId() == null && request.topicId() == null) {
            throw new ValidationException("You must provide either articleId or topicId");
        }

        ForumConfig config = forumConfigRepository.getConfig();
        if (!config.isCommentUnlocked()) {
            throw new BusinessRuleException("Comments are currently disabled");
        }

        Author currentAuthor = authenticationService.getCurrentAuthor();

        if (config.isEmailVerificationRequired()) {
            if (!currentAuthor.isEmailVerified()) {
                throw new BusinessRuleException("You must verify your email before commenting");
            }
        }

        Comment comment;
        Article article = null;
        Topic topic = null;

        if (request.articleId() != null) {
            article = articleRepository.findById(request.articleId());
            if (article == null) {
                throw new ResourceNotFoundException("Article", request.articleId());
            }
            comment = Comment.forArticle(request.message(), article, request.parentId(), request.tags());
        } else {
            topic = topicRepository.findById(request.topicId());
            if (topic == null) {
                throw new ResourceNotFoundException("Topic", request.topicId());
            }
            comment = Comment.forTopic(request.message(), topic, request.parentId(), request.tags());
        }

        Comment savedComment = commentRepository.save(comment);

        authorRepository.addCoinsToCurrentAuthor(config.getCoinsPerComment());

        // Register interactions
        registerCommentInteractions(currentAuthor, savedComment, article, topic, request.parentId());

        return new CreateCommentResponse(
                "Comment created successfully",
                savedComment.getId(),
                savedComment.getCommentContent()
        );
    }

    private void registerCommentInteractions(Author currentAuthor, Comment savedComment,
                                              Article article, Topic topic, java.util.UUID parentId) {
        // If it's a reply to another comment, notify the parent comment owner
        if (parentId != null) {
            Comment parentComment = commentRepository.findById(parentId);
            if (parentComment != null && parentComment.getAuthor() != null) {
                Author parentAuthor = parentComment.getAuthor();
                // Don't notify yourself
                if (!parentAuthor.getId().equals(currentAuthor.getId())) {
                    String targetTitle = article != null ? article.getTitle() : (topic != null ? topic.getTitle() : null);
                    String targetSlug = article != null ? article.getSlug() : (topic != null ? topic.getSlug() : null);

                    AuthorInteraction replyInteraction = AuthorInteraction.create(
                            parentAuthor.getId(),
                            currentAuthor.getId(),
                            InteractionType.REPLY_TO_COMMENT,
                            TargetType.COMMENT,
                            savedComment.getId(),
                            targetTitle,
                            targetSlug
                    );
                    authorInteractionRepository.save(replyInteraction);
                }
            }
        }

        // Notify the article/topic owner about the new comment
        if (article != null && article.getAuthor() != null) {
            Author articleOwner = article.getAuthor();
            // Don't notify yourself
            if (!articleOwner.getId().equals(currentAuthor.getId())) {
                AuthorInteraction interaction = AuthorInteraction.create(
                        articleOwner.getId(),
                        currentAuthor.getId(),
                        InteractionType.COMMENT_ON_ARTICLE,
                        TargetType.ARTICLE,
                        article.getId(),
                        article.getTitle(),
                        article.getSlug()
                );
                authorInteractionRepository.save(interaction);
            }
        } else if (topic != null && topic.getAuthor() != null) {
            Author topicOwner = topic.getAuthor();
            // Don't notify yourself
            if (!topicOwner.getId().equals(currentAuthor.getId())) {
                AuthorInteraction interaction = AuthorInteraction.create(
                        topicOwner.getId(),
                        currentAuthor.getId(),
                        InteractionType.COMMENT_ON_TOPIC,
                        TargetType.TOPIC,
                        topic.getId(),
                        topic.getTitle(),
                        topic.getSlug()
                );
                authorInteractionRepository.save(interaction);
            }
        }
    }
}
