package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.ports.dto.EditArticleRequest;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.services.StorageService;
import dev.falae.application.utils.SlugUtils;

import java.util.UUID;

public class EditArticleUseCase {

    private final ArticleRepository articleRepository;
    private final StorageService storageService;

    public EditArticleUseCase(ArticleRepository articleRepository, StorageService storageService) {
        this.articleRepository = articleRepository;
        this.storageService = storageService;
    }

    public void edit(EditArticleRequest request) {
        UUID articleId = request.articleId();

        String title = request.title();
        String slug = null;
        if (title != null) {
            title = title.trim();
            slug = SlugUtils.toSlug(title);
            if (articleRepository.existsByCurrentAuthorAndTitleExcludingId(title, articleId)) {
                throw new BusinessRuleException("You already have an article with this title");
            }
        }

        if (request.deletedImagePaths() != null && !request.deletedImagePaths().isEmpty()) {
            storageService.deleteFiles(request.deletedImagePaths());
        }

        articleRepository.update(
                articleId,
                title,
                slug,
                request.description(),
                request.tags(),
                request.originalPost(),
                request.deletedImagePaths()
        );
    }
}
