package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.application.ports.services.StorageService;
import dev.falae.core.domain.entities.Article;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.ForumConfig;

import java.util.UUID;

public class DeleteArticleUseCase {

    private final ArticleRepository articleRepository;
    private final AuthenticationService authenticationService;
    private final StorageService storageService;
    private final ForumConfigRepository forumConfigRepository;
    private final AuthorRepository authorRepository;

    public DeleteArticleUseCase(ArticleRepository articleRepository,
                                AuthenticationService authenticationService,
                                StorageService storageService,
                                ForumConfigRepository forumConfigRepository,
                                AuthorRepository authorRepository) {
        this.articleRepository = articleRepository;
        this.authenticationService = authenticationService;
        this.storageService = storageService;
        this.forumConfigRepository = forumConfigRepository;
        this.authorRepository = authorRepository;
    }

    public void delete(UUID articleId) {
        Author currentAuthor = authenticationService.getCurrentAuthor();
        Article article = articleRepository.findById(articleId);

        String folderPath = currentAuthor.getId() + "/" + article.getId();
        storageService.deleteFolder(folderPath);

        articleRepository.deleteById(articleId);

        ForumConfig config = forumConfigRepository.getConfig();
        authorRepository.removeCoinsFromCurrentAuthor(config.getCoinsPerArticle());
    }
}
