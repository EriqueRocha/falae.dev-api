package dev.falae.application.usecases;

import dev.falae.application.ports.dto.TopicResponse;
import dev.falae.application.ports.repositories.TopicRepository;

import java.util.UUID;

public class FindTopicUseCase {

    private final TopicRepository topicRepository;

    public FindTopicUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public TopicResponse findById(UUID topicId) {
        return topicRepository.findTopicResponseById(topicId);
    }

    public TopicResponse findByAuthorUserNameAndSlug(String userName, String slug) {
        return topicRepository.findTopicResponseByAuthorUserNameAndSlug(userName, slug);
    }
}
