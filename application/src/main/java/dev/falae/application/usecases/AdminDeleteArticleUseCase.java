package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.services.StorageService;
import dev.falae.core.domain.entities.Article;

import java.util.UUID;

public class AdminDeleteArticleUseCase {

    private final ArticleRepository articleRepository;
    private final StorageService storageService;

    public AdminDeleteArticleUseCase(ArticleRepository articleRepository, StorageService storageService) {
        this.articleRepository = articleRepository;
        this.storageService = storageService;
    }

    public void delete(UUID articleId) {
        Article article = articleRepository.findById(articleId);

        String folderPath = article.getAuthor().getId() + "/" + article.getId();
        storageService.deleteFolder(folderPath);

        articleRepository.adminDeleteById(articleId);
    }
}
