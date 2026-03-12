package dev.falae.application.usecases;

import dev.falae.application.ports.dto.InteractionResponse;
import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.repositories.TopicRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.AuthorInteraction;
import dev.falae.core.domain.entities.Topic;
import dev.falae.core.domain.valueobjects.InteractionType;
import dev.falae.core.domain.valueobjects.TargetType;

import java.util.UUID;

public class TopicInteractionUseCase {

    private final TopicRepository topicRepository;
    private final AuthorInteractionRepository authorInteractionRepository;
    private final AuthenticationService authenticationService;

    public TopicInteractionUseCase(TopicRepository topicRepository,
                                    AuthorInteractionRepository authorInteractionRepository,
                                    AuthenticationService authenticationService) {
        this.topicRepository = topicRepository;
        this.authorInteractionRepository = authorInteractionRepository;
        this.authenticationService = authenticationService;
    }

    public InteractionResponse toggleLike(UUID topicId) {
        boolean isNowLiked = topicRepository.toggleLike(topicId);

        Author currentAuthor = authenticationService.getCurrentAuthor();
        Topic topic = topicRepository.findById(topicId);

        if (topic != null && topic.getAuthor() != null
                && !topic.getAuthor().getId().equals(currentAuthor.getId())) {
            if (isNowLiked) {
                AuthorInteraction interaction = AuthorInteraction.create(
                        topic.getAuthor().getId(),
                        currentAuthor.getId(),
                        InteractionType.LIKE_TOPIC,
                        TargetType.TOPIC,
                        topic.getId(),
                        topic.getTitle(),
                        topic.getSlug()
                );
                authorInteractionRepository.save(interaction);
            } else {
                authorInteractionRepository.delete(currentAuthor.getId(), InteractionType.LIKE_TOPIC, topicId);
            }
        }

        return new InteractionResponse(isNowLiked, isNowLiked ? "Topic liked" : "Topic unliked");
    }

    public InteractionResponse toggleDislike(UUID topicId) {
        boolean isNowDisliked = topicRepository.toggleDislike(topicId);
        return new InteractionResponse(isNowDisliked, isNowDisliked ? "Topic disliked" : "Topic undisliked");
    }
}
