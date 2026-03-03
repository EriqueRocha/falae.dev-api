package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.TopicRepository;

import java.util.UUID;

public class DeleteTopicUseCase {

    private final TopicRepository topicRepository;

    public DeleteTopicUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public void delete(UUID topicId){
        topicRepository.deleteById(topicId);
    }

}
