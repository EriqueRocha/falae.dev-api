package dev.falae.application.ports.repositories;

import dev.falae.application.ports.dto.ArticlePageResponse;
import dev.falae.application.ports.dto.ArticleResponse;
import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.core.domain.entities.Article;

import java.util.List;
import java.util.UUID;

public interface ArticleRepository {

    Article save(Article article);
    void saveArticleContent(Article article, String contentArticlePath);
    void saveArticleCover(Article article, String articleCoverPath);
    void addArticleImage(Article article, String articleImagePath);
    Article findById(UUID articleId);
    ArticleResponse findArticleResponseById(UUID articleId);
    ArticlePageResponse findAll(int page, int size, FeedSortType sortType);
    ArticlePageResponse findByAuthorUserName(String userName, int page, int size, AuthorContentSortType sortType);
    ArticlePageResponse searchByTitle(String title, int page, int size, AuthorContentSortType sortType);
    long countByAuthorId(UUID authorId);
    void deleteById(UUID articleId);
    void adminDeleteById(UUID articleId);
    long count();
    boolean toggleLike(UUID articleId);
    boolean toggleDislike(UUID articleId);
    boolean toggleSave(UUID articleId);
    boolean existsByCurrentAuthorAndTitle(String title);
    boolean existsByCurrentAuthorAndTitleExcludingId(String title, UUID excludeArticleId);
    boolean hasComments(UUID articleId);
    ArticleResponse findByAuthorUserNameAndSlug(String userName, String slug);
    void update(UUID articleId, String title, String slug, String description, List<String> tags, String originalPost, List<String> deletedImagePaths);
}
