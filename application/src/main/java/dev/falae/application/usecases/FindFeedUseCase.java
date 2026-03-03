package dev.falae.application.usecases;

import dev.falae.application.ports.dto.FeedItemResponse;
import dev.falae.application.ports.dto.FeedPageResponse;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.repositories.FeedRepository;

public class FindFeedUseCase {

    private final FeedRepository feedRepository;

    public FindFeedUseCase(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public FeedPageResponse findAll(int page, int size, FeedSortType sortType, FeedItemResponse.FeedItemType filterType) {
        return feedRepository.findAll(page, size, sortType, filterType);
    }
}
