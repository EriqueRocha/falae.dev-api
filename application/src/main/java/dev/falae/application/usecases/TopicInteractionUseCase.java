package dev.falae.application.usecases;

import dev.falae.application.ports.dto.InteractionResponse;
import dev.falae.application.ports.repositories.TopicRepository;

import java.util.UUID;

public class TopicInteractionUseCase {

    private final TopicRepository topicRepository;

    public TopicInteractionUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public InteractionResponse toggleLike(UUID topicId) {
        boolean isNowLiked = topicRepository.toggleLike(topicId);
        return new InteractionResponse(isNowLiked, isNowLiked ? "Topic liked" : "Topic unliked");
    }

    public InteractionResponse toggleDislike(UUID topicId) {
        boolean isNowDisliked = topicRepository.toggleDislike(topicId);
        return new InteractionResponse(isNowDisliked, isNowDisliked ? "Topic disliked" : "Topic undisliked");
    }
}
