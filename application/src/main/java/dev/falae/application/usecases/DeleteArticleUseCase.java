package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.application.ports.services.StorageService;
import dev.falae.core.domain.entities.Article;
import dev.falae.core.domain.entities.Author;

import java.util.UUID;

public class DeleteArticleUseCase {

    private final ArticleRepository articleRepository;
    private final AuthenticationService authenticationService;
    private final StorageService storageService;

    public DeleteArticleUseCase(ArticleRepository articleRepository,
                                AuthenticationService authenticationService,
                                StorageService storageService) {
        this.articleRepository = articleRepository;
        this.authenticationService = authenticationService;
        this.storageService = storageService;
    }

    public void delete(UUID articleId) {
        Author currentAuthor = authenticationService.getCurrentAuthor();
        Article article = articleRepository.findById(articleId);

        String folderPath = currentAuthor.getId() + "/" + article.getId();
        storageService.deleteFolder(folderPath);

        articleRepository.deleteById(articleId);
    }
}
