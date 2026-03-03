package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.ports.dto.CreateArticleResponse;
import dev.falae.application.ports.dto.SaveArticleRequest;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.application.utils.SlugUtils;
import dev.falae.core.domain.entities.Article;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.ForumConfig;

public class CreateArticleUseCase {

    private final ArticleRepository articleRepository;
    private final ForumConfigRepository forumConfigRepository;
    private final AuthorRepository authorRepository;
    private final AuthenticationService authenticationService;

    public CreateArticleUseCase(ArticleRepository articleRepository,
                                ForumConfigRepository forumConfigRepository,
                                AuthorRepository authorRepository,
                                AuthenticationService authenticationService) {
        this.articleRepository = articleRepository;
        this.forumConfigRepository = forumConfigRepository;
        this.authorRepository = authorRepository;
        this.authenticationService = authenticationService;
    }

    public CreateArticleResponse create(SaveArticleRequest request) {
        ForumConfig config = forumConfigRepository.getConfig();
        if (!config.isArticleCreationUnlocked()) {
            throw new BusinessRuleException("Article creation is currently disabled");
        }

        if (config.isEmailVerificationRequired()) {
            Author currentAuthor = authenticationService.getCurrentAuthor();
            if (!currentAuthor.isEmailVerified()) {
                throw new BusinessRuleException("You must verify your email before creating articles");
            }
        }

        String title = request.title().trim();
        String slug = SlugUtils.toSlug(title);

        if (articleRepository.existsByCurrentAuthorAndTitle(title)) {
            throw new BusinessRuleException("You already have an article with this title");
        }

        Article article = new Article(
                title,
                slug,
                request.originalPost(),
                request.tags(),
                request.description()
        );

        Article savedArticle = articleRepository.save(article);

        if (savedArticle == null || savedArticle.getId() == null) {
            throw new BusinessRuleException("Failed to save article");
        }

        authorRepository.addCoinsToCurrentAuthor(config.getCoinsPerArticle());

        return new CreateArticleResponse(
                "Article saved successfully",
                savedArticle.getId(),
                savedArticle.getTitle(),
                savedArticle.getSlug()
        );
    }
}
