package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.ports.dto.EditTopicRequest;
import dev.falae.application.ports.repositories.TopicRepository;
import dev.falae.application.utils.SlugUtils;

import java.util.UUID;

public class EditTopicUseCase {

    private final TopicRepository topicRepository;

    public EditTopicUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public void edit(EditTopicRequest request) {
        UUID topicId = request.topicId();

        String title = request.title();
        String slug = null;
        if (title != null) {
            title = title.trim();
            slug = SlugUtils.toSlug(title);
            if (topicRepository.existsByCurrentAuthorAndTitleExcludingId(title, topicId)) {
                throw new BusinessRuleException("You already have a topic with this title");
            }
        }

        topicRepository.update(
                topicId,
                title,
                slug,
                request.topicContent(),
                request.tags()
        );
    }
}
