package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.application.ports.repositories.TopicRepository;
import dev.falae.core.domain.entities.ForumConfig;

import java.util.UUID;

public class DeleteTopicUseCase {

    private final TopicRepository topicRepository;
    private final ForumConfigRepository forumConfigRepository;
    private final AuthorRepository authorRepository;

    public DeleteTopicUseCase(TopicRepository topicRepository,
                              ForumConfigRepository forumConfigRepository,
                              AuthorRepository authorRepository) {
        this.topicRepository = topicRepository;
        this.forumConfigRepository = forumConfigRepository;
        this.authorRepository = authorRepository;
    }

    public void delete(UUID topicId){
        topicRepository.deleteById(topicId);

        ForumConfig config = forumConfigRepository.getConfig();
        authorRepository.removeCoinsFromCurrentAuthor(config.getCoinsPerTopic());
    }

}
