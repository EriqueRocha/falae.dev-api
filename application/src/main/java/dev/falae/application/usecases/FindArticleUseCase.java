package dev.falae.application.usecases;

import dev.falae.application.ports.dto.ArticleResponse;
import dev.falae.application.ports.repositories.ArticleRepository;

import java.util.UUID;

public class FindArticleUseCase {

    private final ArticleRepository articleRepository;

    public FindArticleUseCase(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticleResponse findById(UUID articleId) {
        return articleRepository.findArticleResponseById(articleId);
    }

    public ArticleResponse findByAuthorUserNameAndSlug(String userName, String slug) {
        return articleRepository.findByAuthorUserNameAndSlug(userName, slug);
    }
}
