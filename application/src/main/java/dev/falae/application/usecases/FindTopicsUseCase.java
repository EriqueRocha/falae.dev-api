package dev.falae.application.usecases;

import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.TopicPageResponse;
import dev.falae.application.ports.repositories.TopicRepository;

public class FindTopicsUseCase {

    private final TopicRepository topicRepository;

    public FindTopicsUseCase(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public TopicPageResponse findAll(int page, int size, FeedSortType sortType) {
        return topicRepository.findAll(page, size, sortType);
    }

    public TopicPageResponse searchByTitle(String title, int page, int size, AuthorContentSortType sortType) {
        return topicRepository.searchByTitle(title, page, size, sortType);
    }
}
