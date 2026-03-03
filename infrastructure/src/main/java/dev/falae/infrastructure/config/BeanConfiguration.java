package dev.falae.infrastructure.config;

import dev.falae.application.ports.repositories.*;
import dev.falae.application.ports.services.*;
import dev.falae.application.usecases.*;
import dev.falae.application.usecases.SearchByTagsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public AuthenticateAuthorUseCase authenticateAuthorUseCase(
            AuthorRepository authorRepository,
            PasswordService passwordService,
            TokenService tokenService) {
        return new AuthenticateAuthorUseCase(authorRepository, passwordService, tokenService);
    }

    @Bean
    public AuthenticateAdminUseCase authenticateAdminUseCase(
            AdminRepository adminRepository,
            PasswordService passwordService,
            TokenService tokenService) {
        return new AuthenticateAdminUseCase(adminRepository, passwordService, tokenService);
    }

    @Bean
    public CreateAuthorUseCase createAuthorUseCase(
            AuthorRepository authorRepository,
            PasswordService passwordService,
            SendEmailService sendEmailService,
            TokenService tokenService) {
        return new CreateAuthorUseCase(authorRepository, passwordService, sendEmailService, tokenService);
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(
            AuthorRepository authorRepository,
            TokenService tokenService) {
        return new VerifyEmailUseCase(authorRepository, tokenService);
    }

    @Bean
    public ResendVerificationEmailUseCase resendVerificationEmailUseCase(
            AuthorRepository authorRepository,
            TokenService tokenService,
            SendEmailService sendEmailService) {
        return new ResendVerificationEmailUseCase(authorRepository, tokenService, sendEmailService);
    }

    @Bean
    public GoogleAuthUseCase googleAuthUseCase(
            AuthorRepository authorRepository,
            GoogleTokenVerificationService googleTokenVerificationService,
            TokenService tokenService,
            SendEmailService sendEmailService) {
        return new GoogleAuthUseCase(authorRepository, googleTokenVerificationService, tokenService, sendEmailService);
    }

    @Bean
    public CreateArticleUseCase createArticleUseCase(
            ArticleRepository articleRepository,
            ForumConfigRepository forumConfigRepository,
            AuthorRepository authorRepository,
            AuthenticationService authenticationService) {
        return new CreateArticleUseCase(articleRepository, forumConfigRepository, authorRepository, authenticationService);
    }

    @Bean
    public EditArticleUseCase editArticleUseCase(
            ArticleRepository articleRepository,
            StorageService storageService) {
        return new EditArticleUseCase(articleRepository, storageService);
    }

    @Bean
    public UpdateAuthorUseCase updateAuthorUseCase(
            AuthorRepository authorRepository,
            AuthenticationService authenticationService) {
        return new UpdateAuthorUseCase(authorRepository, authenticationService);
    }

    @Bean
    public UpdateAuthorProfileUseCase updateAuthorProfileUseCase(
            AuthorRepository authorRepository,
            AuthenticationService authenticationService,
            PasswordService passwordService) {
        return new UpdateAuthorProfileUseCase(authorRepository, authenticationService, passwordService);
    }

    @Bean
    public CreateCommentUseCase createCommentUseCase(
            CommentRepository commentRepository,
            ArticleRepository articleRepository,
            TopicRepository topicRepository,
            ForumConfigRepository forumConfigRepository,
            AuthorRepository authorRepository,
            AuthenticationService authenticationService) {
        return new CreateCommentUseCase(commentRepository, articleRepository, topicRepository, forumConfigRepository, authorRepository, authenticationService);
    }

    @Bean
    public CreateTopicUseCase createTopicUseCase(TopicRepository topicRepository,
                                                  ForumConfigRepository forumConfigRepository,
                                                  AuthorRepository authorRepository,
                                                  AuthenticationService authenticationService){
        return new CreateTopicUseCase(topicRepository, forumConfigRepository, authorRepository, authenticationService);
    }

    @Bean
    public EditTopicUseCase editTopicUseCase(TopicRepository topicRepository) {
        return new EditTopicUseCase(topicRepository);
    }

    @Bean
    public DeleteTopicUseCase DeleteTopicUseCase(TopicRepository topicRepository){
        return new DeleteTopicUseCase(topicRepository);
    }

    @Bean
    public DeleteArticleUseCase deleteArticleUseCase(ArticleRepository articleRepository,
                                                     AuthenticationService authenticationService,
                                                     StorageService storageService) {
        return new DeleteArticleUseCase(articleRepository, authenticationService, storageService);
    }

    @Bean
    public FindCommentsUseCase findCommentsUseCase(CommentRepository commentRepository) {
        return new FindCommentsUseCase(commentRepository);
    }

    @Bean
    public DeleteAuthorUseCase deleteAuthorUseCase(AuthorRepository authorRepository) {
        return new DeleteAuthorUseCase(authorRepository);
    }

    @Bean
    public DeleteCommentUseCase deleteCommentUseCase(CommentRepository commentRepository) {
        return new DeleteCommentUseCase(commentRepository);
    }

    @Bean
    public AdminDeleteArticleUseCase adminDeleteArticleUseCase(ArticleRepository articleRepository,
                                                               StorageService storageService) {
        return new AdminDeleteArticleUseCase(articleRepository, storageService);
    }

    @Bean
    public AdminDeleteTopicUseCase adminDeleteTopicUseCase(TopicRepository topicRepository) {
        return new AdminDeleteTopicUseCase(topicRepository);
    }

    @Bean
    public AdminDeleteCommentUseCase adminDeleteCommentUseCase(CommentRepository commentRepository) {
        return new AdminDeleteCommentUseCase(commentRepository);
    }

    @Bean
    public GetForumStatsUseCase getForumStatsUseCase(
            ArticleRepository articleRepository,
            TopicRepository topicRepository,
            CommentRepository commentRepository) {
        return new GetForumStatsUseCase(articleRepository, topicRepository, commentRepository);
    }

    @Bean
    public FindArticleUseCase findArticleUseCase(ArticleRepository articleRepository) {
        return new FindArticleUseCase(articleRepository);
    }

    @Bean
    public FindTopicUseCase findTopicUseCase(TopicRepository topicRepository) {
        return new FindTopicUseCase(topicRepository);
    }

    @Bean
    public ArticleInteractionUseCase articleInteractionUseCase(ArticleRepository articleRepository) {
        return new ArticleInteractionUseCase(articleRepository);
    }

    @Bean
    public TopicInteractionUseCase topicInteractionUseCase(TopicRepository topicRepository) {
        return new TopicInteractionUseCase(topicRepository);
    }

    @Bean
    public CommentInteractionUseCase commentInteractionUseCase(CommentRepository commentRepository) {
        return new CommentInteractionUseCase(commentRepository);
    }

    @Bean
    public FindArticlesUseCase findArticlesUseCase(ArticleRepository articleRepository) {
        return new FindArticlesUseCase(articleRepository);
    }

    @Bean
    public FindTopicsUseCase findTopicsUseCase(TopicRepository topicRepository) {
        return new FindTopicsUseCase(topicRepository);
    }

    @Bean
    public FindFeedUseCase findFeedUseCase(FeedRepository feedRepository) {
        return new FindFeedUseCase(feedRepository);
    }

    @Bean
    public GetAuthorProfileUseCase getAuthorProfileUseCase(AuthorRepository authorRepository) {
        return new GetAuthorProfileUseCase(authorRepository);
    }

    @Bean
    public GetAuthorArticlesUseCase getAuthorArticlesUseCase(ArticleRepository articleRepository) {
        return new GetAuthorArticlesUseCase(articleRepository);
    }

    @Bean
    public GetAuthorTopicsUseCase getAuthorTopicsUseCase(TopicRepository topicRepository) {
        return new GetAuthorTopicsUseCase(topicRepository);
    }

    @Bean
    public GetAuthorCommentsUseCase getAuthorCommentsUseCase(CommentRepository commentRepository) {
        return new GetAuthorCommentsUseCase(commentRepository);
    }

    @Bean
    public SearchByTagsUseCase searchByTagsUseCase(SearchRepository searchRepository) {
        return new SearchByTagsUseCase(searchRepository);
    }
}
