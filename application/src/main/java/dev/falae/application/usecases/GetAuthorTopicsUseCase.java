package dev.falae.application.usecases;

import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.TopicPageResponse;
import dev.falae.application.ports.repositories.TopicRepository;

public class GetAuthorTopicsUseCase {

    private final TopicRepository topicRepository;

    public GetAuthorTopicsUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public TopicPageResponse execute(String userName, int page, int size, AuthorContentSortType sortType) {
        return topicRepository.findByAuthorUserName(userName, page, size, sortType);
    }
}
