package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.ports.dto.CreateTopicRequest;
import dev.falae.application.ports.dto.CreateTopicResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.repositories.ForumConfigRepository;
import dev.falae.application.ports.repositories.TopicRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.application.utils.SlugUtils;
import dev.falae.core.domain.entities.Author;
import dev.falae.core.domain.entities.ForumConfig;
import dev.falae.core.domain.entities.Topic;

public class CreateTopicUseCase {

    private final TopicRepository topicRepository;
    private final ForumConfigRepository forumConfigRepository;
    private final AuthorRepository authorRepository;
    private final AuthenticationService authenticationService;

    public CreateTopicUseCase(TopicRepository topicRepository,
                              ForumConfigRepository forumConfigRepository,
                              AuthorRepository authorRepository,
                              AuthenticationService authenticationService) {
        this.topicRepository = topicRepository;
        this.forumConfigRepository = forumConfigRepository;
        this.authorRepository = authorRepository;
        this.authenticationService = authenticationService;
    }

    public CreateTopicResponse create(CreateTopicRequest createTopicRequest){
        ForumConfig config = forumConfigRepository.getConfig();
        if (!config.isTopicCreationUnlocked()) {
            throw new BusinessRuleException("Topic creation is currently disabled");
        }

        if (config.isEmailVerificationRequired()) {
            Author currentAuthor = authenticationService.getCurrentAuthor();
            if (!currentAuthor.isEmailVerified()) {
                throw new BusinessRuleException("You must verify your email before creating topics");
            }
        }

        String title = createTopicRequest.title().trim();
        String slug = SlugUtils.toSlug(title);

        if (topicRepository.existsByCurrentAuthorAndTitle(title)) {
            throw new BusinessRuleException("You already have a topic with this title");
        }

        Topic topic = new Topic(
                createTopicRequest.topicContent(),
                title,
                slug,
                createTopicRequest.tags()
        );

        Topic savedTopic = topicRepository.save(topic);

        authorRepository.addCoinsToCurrentAuthor(config.getCoinsPerTopic());

        return new CreateTopicResponse(savedTopic.getId(), savedTopic.getTitle(), savedTopic.getSlug());
    }
}
