package dev.falae.application.usecases;

import dev.falae.application.ports.dto.ArticlePageResponse;
import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.repositories.ArticleRepository;

public class GetPrivateArticlesUseCase {

    private final ArticleRepository articleRepository;

    public GetPrivateArticlesUseCase(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticlePageResponse execute(int page, int size, AuthorContentSortType sortType) {
        return articleRepository.findPrivateByCurrentAuthor(page, size, sortType);
    }
}
