package dev.falae.application.usecases;

import dev.falae.application.ports.dto.InteractionResponse;
import dev.falae.application.ports.repositories.ArticleRepository;
import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Article;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.AuthorInteraction;
import dev.falae.core.domain.valueobjects.InteractionType;
import dev.falae.core.domain.valueobjects.TargetType;

import java.util.UUID;

public class ArticleInteractionUseCase {

    private final ArticleRepository articleRepository;
    private final AuthorInteractionRepository authorInteractionRepository;
    private final AuthenticationService authenticationService;

    public ArticleInteractionUseCase(ArticleRepository articleRepository,
                                      AuthorInteractionRepository authorInteractionRepository,
                                      AuthenticationService authenticationService) {
        this.articleRepository = articleRepository;
        this.authorInteractionRepository = authorInteractionRepository;
        this.authenticationService = authenticationService;
    }

    public InteractionResponse toggleLike(UUID articleId) {
        boolean isNowLiked = articleRepository.toggleLike(articleId);

        Author currentAuthor = authenticationService.getCurrentAuthor();
        Article article = articleRepository.findById(articleId);

        if (article != null && article.getAuthor() != null
                && !article.getAuthor().getId().equals(currentAuthor.getId())) {
            if (isNowLiked) {
                AuthorInteraction interaction = AuthorInteraction.create(
                        article.getAuthor().getId(),
                        currentAuthor.getId(),
                        InteractionType.LIKE_ARTICLE,
                        TargetType.ARTICLE,
                        article.getId(),
                        article.getTitle(),
                        article.getSlug()
                );
                authorInteractionRepository.save(interaction);
            } else {
                authorInteractionRepository.delete(currentAuthor.getId(), InteractionType.LIKE_ARTICLE, articleId);
            }
        }

        return new InteractionResponse(isNowLiked, isNowLiked ? "Article liked" : "Article unliked");
    }

    public InteractionResponse toggleSave(UUID articleId) {
        boolean isNowSaved = articleRepository.toggleSave(articleId);

        Author currentAuthor = authenticationService.getCurrentAuthor();
        Article article = articleRepository.findById(articleId);

        if (article != null && article.getAuthor() != null
                && !article.getAuthor().getId().equals(currentAuthor.getId())) {
            if (isNowSaved) {
                AuthorInteraction interaction = AuthorInteraction.create(
                        article.getAuthor().getId(),
                        currentAuthor.getId(),
                        InteractionType.SAVE_ARTICLE,
                        TargetType.ARTICLE,
                        article.getId(),
                        article.getTitle(),
                        article.getSlug()
                );
                authorInteractionRepository.save(interaction);
            } else {
                authorInteractionRepository.delete(currentAuthor.getId(), InteractionType.SAVE_ARTICLE, articleId);
            }
        }

        return new InteractionResponse(isNowSaved, isNowSaved ? "Article saved" : "Article unsaved");
    }

    public InteractionResponse toggleDislike(UUID articleId) {
        boolean isNowDisliked = articleRepository.toggleDislike(articleId);
        return new InteractionResponse(isNowDisliked, isNowDisliked ? "Article disliked" : "Article undisliked");
    }
}
