package dev.falae.application.usecases;

import dev.falae.application.ports.dto.InteractionResponse;
import dev.falae.application.ports.repositories.ArticleRepository;

import java.util.UUID;

public class ArticleInteractionUseCase {

    private final ArticleRepository articleRepository;

    public ArticleInteractionUseCase(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public InteractionResponse toggleLike(UUID articleId) {
        boolean isNowLiked = articleRepository.toggleLike(articleId);
        return new InteractionResponse(isNowLiked, isNowLiked ? "Article liked" : "Article unliked");
    }

    public InteractionResponse toggleSave(UUID articleId) {
        boolean isNowSaved = articleRepository.toggleSave(articleId);
        return new InteractionResponse(isNowSaved, isNowSaved ? "Article saved" : "Article unsaved");
    }

    public InteractionResponse toggleDislike(UUID articleId) {
        boolean isNowDisliked = articleRepository.toggleDislike(articleId);
        return new InteractionResponse(isNowDisliked, isNowDisliked ? "Article disliked" : "Article undisliked");
    }
}
