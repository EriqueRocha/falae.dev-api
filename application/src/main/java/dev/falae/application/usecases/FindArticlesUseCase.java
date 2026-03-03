package dev.falae.application.usecases;

import dev.falae.application.ports.dto.ArticlePageResponse;
import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.repositories.ArticleRepository;

public class FindArticlesUseCase {

    private final ArticleRepository articleRepository;

    public FindArticlesUseCase(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public ArticlePageResponse findAll(int page, int size, FeedSortType sortType) {
        return articleRepository.findAll(page, size, sortType);
    }

    public ArticlePageResponse searchByTitle(String title, int page, int size, AuthorContentSortType sortType) {
        return articleRepository.searchByTitle(title, page, size, sortType);
    }
}
