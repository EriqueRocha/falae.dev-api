package dev.falae.application.usecases;

import dev.falae.application.ports.dto.ArticlePageResponse;
import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.repositories.ArticleRepository;

public class GetAuthorArticlesUseCase {

    private final ArticleRepository articleRepository;

    public GetAuthorArticlesUseCase(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticlePageResponse execute(String userName, int page, int size, AuthorContentSortType sortType) {
        return articleRepository.findByAuthorUserName(userName, page, size, sortType);
    }
}
