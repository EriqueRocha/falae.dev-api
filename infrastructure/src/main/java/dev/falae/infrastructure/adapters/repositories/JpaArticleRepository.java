package dev.falae.infrastructure.adapters.repositories;

import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.ports.dto.ArticlePageResponse;
import dev.falae.application.ports.dto.ArticleResponse;
import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.core.domain.entities.Article;
import dev.falae.infrastructure.adapters.repositories.entities.ArticleEntity;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.ArticleJpaRepository;
import dev.falae.infrastructure.config.security.AuthenticatedAuthorProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaArticleRepository implements ArticleRepository {

    private final ArticleJpaRepository articleJpaRepository;
    private final JpaAuthorRepository jpaAuthorRepository;
    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;

    public JpaArticleRepository(ArticleJpaRepository articleJpaRepository, JpaAuthorRepository jpaAuthorRepository, AuthenticatedAuthorProvider authenticatedAuthorProvider) {
        this.articleJpaRepository = articleJpaRepository;
        this.jpaAuthorRepository = jpaAuthorRepository;
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
    }

    @Override
    public Article save(Article article) {
        ArticleEntity entity = toEntity(article);
        articleJpaRepository.save(entity);
        return toDomain(entity);
    }

    public Article toDomain(ArticleEntity entity) {
        return new Article(
                entity.getId(),
                jpaAuthorRepository.toDomain(entity.getAuthor()),
                entity.getCreatedAt(),
                entity.isMarkdown(),
                entity.getTitle(),
                entity.getSlug(),
                entity.getCoverImage(),
                entity.getOriginalPost(),
                entity.getTags(),
                entity.getImagePaths(),
                entity.getDescription(),
                entity.getUrlArticleContent(),
                entity.getLikesCount(),
                entity.getSavesCount(),
                entity.getDislikesCount(),
                entity.getCommentsCount()
        );
    }

    public ArticleEntity toEntity(Article article) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();

        ArticleEntity entity = new ArticleEntity(
                author,
                null,
                article.getTitle(),
                article.getSlug(),
                article.getCoverImage(),
                article.getOriginalPost(),
                article.getTags(),
                article.getImagePaths(),
                article.getDescription(),
                null
        );
        entity.setId(article.getId());
        return entity;
    }

    public Article findById(UUID articleId) {
        ArticleEntity articleEntity = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        return toDomain(articleEntity);
    }

    @Override
    public ArticleResponse findArticleResponseById(UUID articleId) {
        ArticleEntity articleEntity = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        return toArticleResponse(articleEntity);
    }

    @Override
    public ArticlePageResponse findAll(int page, int size, FeedSortType sortType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ArticleEntity> articlePage = switch (sortType) {
            case RECENT -> articleJpaRepository.findAllByOrderByCreatedAtDesc(pageable);
            case OLDEST -> articleJpaRepository.findAllByOrderByCreatedAtAsc(pageable);
            case LIKES -> articleJpaRepository.findAllByOrderByLikesCountDesc(pageable);
        };

        List<ArticleResponse> articles = articlePage.getContent().stream()
                .map(this::toArticleResponse)
                .toList();

        return new ArticlePageResponse(
                articles,
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalElements(),
                articlePage.getTotalPages(),
                articlePage.hasNext()
        );
    }

    @Override
    public ArticlePageResponse findByAuthorUserName(String userName, int page, int size, AuthorContentSortType sortType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ArticleEntity> articlePage = switch (sortType) {
            case RECENT -> articleJpaRepository.findByAuthorUserNameOrderByCreatedAtDesc(userName, pageable);
            case OLDEST -> articleJpaRepository.findByAuthorUserNameOrderByCreatedAtAsc(userName, pageable);
            case LIKES -> articleJpaRepository.findByAuthorUserNameOrderByLikesCountDesc(userName, pageable);
            case SAVES -> articleJpaRepository.findByAuthorUserNameOrderBySavesCountDesc(userName, pageable);
            case COMMENTS -> articleJpaRepository.findByAuthorUserNameOrderByCommentsCountDesc(userName, pageable);
        };

        List<ArticleResponse> articles = articlePage.getContent().stream()
                .map(this::toArticleResponse)
                .toList();

        return new ArticlePageResponse(
                articles,
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalElements(),
                articlePage.getTotalPages(),
                articlePage.hasNext()
        );
    }

    @Override
    public long countByAuthorId(UUID authorId) {
        return articleJpaRepository.countByAuthorId(authorId);
    }

    private ArticleResponse toArticleResponse(ArticleEntity entity) {
        AuthorEntity currentAuthor = authenticatedAuthorProvider.getCurrentAuthorOrNull();

        Boolean isLiked = null;
        Boolean isDisliked = null;
        Boolean isSaved = null;

        if (currentAuthor != null) {
            isLiked = entity.getAuthorsLikedArticle().contains(currentAuthor);
            isDisliked = entity.getAuthorsDislikedArticle().contains(currentAuthor);
            isSaved = entity.getAuthorsSavedArticle().contains(currentAuthor);
        }

        return new ArticleResponse(
                entity.getId(),
                entity.getAuthor() != null ? entity.getAuthor().getId() : null,
                entity.getAuthor() != null ? entity.getAuthor().getName() : null,
                entity.getAuthor() != null ? entity.getAuthor().getUserName() : null,
                entity.getAuthor() != null ? entity.getAuthor().getProfileImageUrl() : null,
                entity.getCreatedAt(),
                entity.isMarkdown(),
                entity.getTitle(),
                entity.getSlug(),
                entity.getCoverImage(),
                entity.getOriginalPost(),
                entity.getTags(),
                entity.getDescription(),
                entity.getUrlArticleContent(),
                entity.getLikesCount(),
                entity.getDislikesCount(),
                entity.getCommentsCount(),
                entity.getSavesCount(),
                isLiked,
                isDisliked,
                isSaved
        );
    }

    @Override
    public void deleteById(UUID articleId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        ArticleEntity article = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        if (!article.getAuthor().getId().equals(author.getId())) {
            throw new ResourceNotFoundException("Article", articleId);
        }

        articleJpaRepository.delete(article);
    }

    @Override
    public void adminDeleteById(UUID articleId) {
        ArticleEntity article = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));
        articleJpaRepository.delete(article);
    }

    @Override
    public void saveArticleContent(Article article, String contentArticlePath) {
        ArticleEntity articleEntity = articleJpaRepository.findById(article.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", article.getId()));

        articleEntity.setUrlArticleContent(contentArticlePath);
        articleEntity.setMarkdown(article.getMarkdown());
        articleJpaRepository.save(articleEntity);
    }

    @Override
    public void saveArticleCover(Article article, String articleCoverPath) {
        ArticleEntity articleEntity = articleJpaRepository.findById(article.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", article.getId()));

        articleEntity.setCoverImage(articleCoverPath);
        articleJpaRepository.save(articleEntity);
    }

    @Override
    public void addArticleImage(Article article, String articleImagePath) {
        ArticleEntity articleEntity = articleJpaRepository.findById(article.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", article.getId()));

        articleEntity.getImagePaths().add(articleImagePath);
        articleJpaRepository.save(articleEntity);
    }

    public ArticleEntity getReferenceById(UUID articleId){
        return articleJpaRepository.getReferenceById(articleId);
    }

    @Override
    public long count() {
        return articleJpaRepository.count();
    }

    @Override
    public boolean toggleLike(UUID articleId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        ArticleEntity article = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        boolean wasLiked = article.getAuthorsLikedArticle().contains(author);
        boolean wasDisliked = article.getAuthorsDislikedArticle().contains(author);

        if (wasDisliked) {
            article.getAuthorsDislikedArticle().remove(author);
            article.setDislikesCount(article.getDislikesCount() - 1);
        }

        if (wasLiked) {
            article.getAuthorsLikedArticle().remove(author);
            article.setLikesCount(article.getLikesCount() - 1);
        } else {
            article.getAuthorsLikedArticle().add(author);
            article.setLikesCount(article.getLikesCount() + 1);
        }

        articleJpaRepository.save(article);
        return !wasLiked;
    }

    @Override
    public boolean toggleDislike(UUID articleId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        ArticleEntity article = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        boolean wasDisliked = article.getAuthorsDislikedArticle().contains(author);
        boolean wasLiked = article.getAuthorsLikedArticle().contains(author);

        if (wasLiked) {
            article.getAuthorsLikedArticle().remove(author);
            article.setLikesCount(article.getLikesCount() - 1);
        }

        if (wasDisliked) {
            article.getAuthorsDislikedArticle().remove(author);
            article.setDislikesCount(article.getDislikesCount() - 1);
        } else {
            article.getAuthorsDislikedArticle().add(author);
            article.setDislikesCount(article.getDislikesCount() + 1);
        }

        articleJpaRepository.save(article);
        return !wasDisliked;
    }

    @Override
    public boolean toggleSave(UUID articleId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        ArticleEntity article = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        boolean wasSaved = article.getAuthorsSavedArticle().contains(author);

        if (wasSaved) {
            article.getAuthorsSavedArticle().remove(author);
            article.setSavesCount(article.getSavesCount() - 1);
        } else {
            article.getAuthorsSavedArticle().add(author);
            article.setSavesCount(article.getSavesCount() + 1);
        }

        articleJpaRepository.save(article);
        return !wasSaved;
    }

    @Override
    public boolean existsByCurrentAuthorAndTitle(String title) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        return articleJpaRepository.existsByAuthorIdAndTitle(author.getId(), title);
    }

    @Override
    public ArticlePageResponse searchByTitle(String title, int page, int size, AuthorContentSortType sortType) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ArticleEntity> articlePage = switch (sortType) {
            case RECENT -> articleJpaRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(title, pageable);
            case OLDEST -> articleJpaRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtAsc(title, pageable);
            case LIKES -> articleJpaRepository.findByTitleContainingIgnoreCaseOrderByLikesCountDesc(title, pageable);
            case SAVES -> articleJpaRepository.findByTitleContainingIgnoreCaseOrderBySavesCountDesc(title, pageable);
            case COMMENTS -> articleJpaRepository.findByTitleContainingIgnoreCaseOrderByCommentsCountDesc(title, pageable);
        };

        List<ArticleResponse> articles = articlePage.getContent().stream()
                .map(this::toArticleResponse)
                .toList();

        return new ArticlePageResponse(
                articles,
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalElements(),
                articlePage.getTotalPages(),
                articlePage.hasNext()
        );
    }

    @Override
    public boolean hasComments(UUID articleId) {
        ArticleEntity article = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));
        return article.getComments() != null && !article.getComments().isEmpty();
    }

    @Override
    public ArticleResponse findByAuthorUserNameAndSlug(String userName, String slug) {
        ArticleEntity entity = articleJpaRepository.findByAuthorUserNameAndSlug(userName, slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found for author: " + userName + " with slug: " + slug));
        return toArticleResponse(entity);
    }

    @Override
    public boolean existsByCurrentAuthorAndTitleExcludingId(String title, UUID excludeArticleId) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        return articleJpaRepository.existsByAuthorIdAndTitleAndIdNot(author.getId(), title, excludeArticleId);
    }

    @Override
    public void update(UUID articleId, String title, String slug, String description, List<String> tags, String originalPost, List<String> deletedImagePaths) {
        AuthorEntity author = authenticatedAuthorProvider.getCurrentAuthor();
        ArticleEntity articleEntity = articleJpaRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        if (!articleEntity.getAuthor().getId().equals(author.getId())) {
            throw new ResourceNotFoundException("Article", articleId);
        }

        if (title != null) {
            articleEntity.setTitle(title);
            articleEntity.setSlug(slug);
        }
        if (description != null) {
            articleEntity.setDescription(description);
        }
        if (tags != null) {
            articleEntity.setTags(tags);
        }
        if (originalPost != null) {
            articleEntity.setOriginalPost(originalPost);
        }
        if (deletedImagePaths != null && !deletedImagePaths.isEmpty()) {
            List<String> currentImagePaths = articleEntity.getImagePaths();
            if (currentImagePaths != null) {
                currentImagePaths.removeAll(deletedImagePaths);
            }
        }

        articleJpaRepository.save(articleEntity);
    }
}
