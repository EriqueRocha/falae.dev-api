package dev.falae.application.ports.repositories;

import dev.falae.application.ports.dto.FeedItemResponse;
import dev.falae.application.ports.dto.FeedPageResponse;
import dev.falae.application.ports.dto.FeedSortType;

public interface FeedRepository {

    FeedPageResponse findAll(int page, int size, FeedSortType sortType, FeedItemResponse.FeedItemType filterType);
}
