package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.TopicRepository;

import java.util.UUID;

public class AdminDeleteTopicUseCase {

    private final TopicRepository topicRepository;

    public AdminDeleteTopicUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public void delete(UUID topicId) {
        topicRepository.adminDeleteById(topicId);
    }

}
