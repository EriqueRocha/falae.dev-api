package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.core.domain.entities.Article;
import dev.falae.core.domain.entities.ForumConfig;

import java.util.UUID;

public class PublishArticleUseCase {

    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;
    private final ForumConfigRepository forumConfigRepository;

    public PublishArticleUseCase(ArticleRepository articleRepository,
                                  AuthorRepository authorRepository,
                                  ForumConfigRepository forumConfigRepository) {
        this.articleRepository = articleRepository;
        this.authorRepository = authorRepository;
        this.forumConfigRepository = forumConfigRepository;
    }

    public void execute(UUID articleId) {
        Article article = articleRepository.findById(articleId);

        if (!Boolean.TRUE.equals(article.getIsPrivate())) {
            throw new BusinessRuleException("Article is already public");
        }

        articleRepository.publishArticle(articleId);

        ForumConfig config = forumConfigRepository.getConfig();
        authorRepository.addCoinsToCurrentAuthor(config.getCoinsPerArticle());
    }
}
